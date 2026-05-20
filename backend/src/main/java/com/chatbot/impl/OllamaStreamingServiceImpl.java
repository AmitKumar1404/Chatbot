package com.chatbot.impl;

import com.chatbot.service.OllamaStreamingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
// ADDED: timeout support
import java.time.Duration;

// ADDED: shared constants
import static com.chatbot.constant.StreamConstants.ERROR_PREFIX;
import static com.chatbot.constant.StreamConstants.isTransientStreamingFailure;
import java.util.Map;

@Service
public class OllamaStreamingServiceImpl implements OllamaStreamingService {

    private static final Logger log = LoggerFactory.getLogger(OllamaStreamingServiceImpl.class);
    private static final String GENERATE_PATH = "/api/generate";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OllamaStreamingServiceImpl(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${app.ai.ollama.base-url}") String baseUrl,
            @Value("${app.ai.ollama.model}") String model) {

        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.webClient = webClientBuilder.baseUrl(normalizedBaseUrl).build();
        this.objectMapper = objectMapper;
        this.model = model;

        log.info("OllamaStreamingServiceImpl initialized: baseUrl='{}', model='{}'", normalizedBaseUrl, model);
    }

    @Override
    public Flux<String> streamChat(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return Flux.just(ERROR_PREFIX + "Message must not be empty.");
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", userMessage,
                "stream", true
        );

        log.info("Ollama streaming request: model='{}', msgLength={}", model, userMessage.length());

        // exchangeToFlux ties the ClientResponse lifecycle to the returned Flux so that
        // subscriber cancellation reliably closes the inbound HTTP response (Reactor Netty).
        // retrieve().bodyToFlux(...) can leave the exchange open in some streaming edge cases.
        Flux<String> fromOllama = webClient.post()
                .uri(GENERATE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToFlux(response -> {
                    if (response.statusCode().isError()) {
                        log.error("Ollama HTTP error — status={}, model='{}'", response.statusCode(), model);
                        return response.createException().flatMapMany(ex -> Flux.error(ex));
                    }

                    return response.bodyToFlux(String.class)
                            .doOnNext(chunk -> log.debug("RAW CHUNK: {}", chunk))
                            .doOnCancel(() -> log.info(
                                    "Ollama upstream cancelled — model='{}', path='{}'", model, GENERATE_PATH))
                            .doFinally(sig -> {
                                if (sig == SignalType.CANCEL) {
                                    log.info("Ollama HTTP stream disposed (cancel) — model='{}'", model);
                                }
                            })
                            // Concurrency 1: stop parsing beyond the first in-flight line when cancelled.
                            .flatMap(this::mapOllamaLineToTextFlux, 1)
                            .filter(text -> !text.isBlank());
                });

        return fromOllama
                .timeout(Duration.ofSeconds(90))
                .doOnComplete(() -> log.info("Ollama streaming complete for model='{}'", model))
                .doOnError(e -> log.error("Ollama streaming error: {}", e.getMessage(), e))
                // Do not emit ERROR_PREFIX as a "chunk" for transient failures — that bypasses
                // ChatWebSocketController.doOnError and gets persisted via doOnComplete.
                .onErrorResume(e -> isTransientStreamingFailure(e)
                        ? Flux.error(e)
                        : Flux.just(ERROR_PREFIX + "Streaming failed. Please try again."))
                .doFinally(sig -> {
                    if (sig == SignalType.CANCEL) {
                        log.info("Reactor cancellation propagated — model='{}'", model);
                    }
                });
    }

    /**
     * Maps one Ollama NDJSON line to zero or one assistant text fragments.
     */
    private Flux<String> mapOllamaLineToTextFlux(String chunk) {
        try {
            JsonNode root = objectMapper.readTree(chunk);
            String text = root.path("response").asText();

            if (text != null && !text.isBlank()) {
                log.debug("PARSED: {}", text);
                return Flux.just(text);
            }
        } catch (Exception e) {
            log.warn("PARSE FAILED: {}", chunk);
        }
        return Flux.empty();
    }
}
