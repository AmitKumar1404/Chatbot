package com.chatbot.client;

import com.chatbot.model.Message;
import com.chatbot.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.chatbot.constant.AppConstants.AI_SERVICE_ERROR;

@Service
public class OllamaClient implements AIService {

    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);
    private static final String GENERATE_PATH = "/api/generate";
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(5);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String systemPrompt;

    public OllamaClient(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${app.ai.ollama.base-url}") String baseUrl,
            @Value("${app.ai.ollama.model}") String model,
            @Value("${app.ai.ollama.system-prompt}") String systemPrompt) {

        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.webClient = webClientBuilder.baseUrl(normalizedBaseUrl).build();
        this.objectMapper = objectMapper;
        this.model = model;
        this.systemPrompt = systemPrompt;

        log.info("OllamaClient initialized: baseUrl='{}', model='{}'", normalizedBaseUrl, model);
    }

    @Override
    public String chat(String userMessage) {
        return chat(userMessage, List.of());
    }

    /**
     * Sends a prompt to Ollama including the full conversation history for context.
     * The system prompt is prepended to every request.
     *
     * @param userMessage current user input
     * @param history     previous messages for this session, ordered oldest-first
     */
    @Override
    public String chat(String userMessage, List<Message> history) {
        if (userMessage == null || userMessage.isBlank()) {
            return AI_SERVICE_ERROR;
        }

        String prompt = buildPrompt(userMessage, history);
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        log.info("Ollama request: model='{}', historyTurns={}, msgLength={}",
                model, history.size(), userMessage.length());

        try {
            String responseJson = webClient.post()
                    .uri(GENERATE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(REQUEST_TIMEOUT);

            return parseResponse(responseJson);

        } catch (WebClientRequestException e) {
            if (e.getCause() instanceof ConnectException) {
                log.error("Ollama is not running or not reachable. "
                        + "Start Ollama with 'ollama serve' and ensure model '{}' is pulled.", model);
            } else {
                log.error("Ollama network error: {}", e.getMessage(), e);
            }
            return AI_SERVICE_ERROR;

        } catch (WebClientResponseException e) {
            log.error("Ollama HTTP {} error: {}", e.getStatusCode().value(), e.getResponseBodyAsString(), e);
            return AI_SERVICE_ERROR;

        } catch (IllegalStateException e) {
            log.error("Ollama request timed out after {} — model '{}' may still be loading.", REQUEST_TIMEOUT, model);
            return AI_SERVICE_ERROR;

        } catch (Exception e) {
            log.error("Unexpected error calling Ollama: {}", e.getMessage(), e);
            return AI_SERVICE_ERROR;
        }
    }

    /**
     * Builds a plain-text prompt that includes the system instruction, prior conversation
     * turns, and the current user message.
     */
    private String buildPrompt(String userMessage, List<Message> history) {
        StringBuilder sb = new StringBuilder();
        sb.append(systemPrompt).append("\n\n");

        for (Message msg : history) {
            sb.append("User: ").append(msg.getUserMessage()).append("\n");
            sb.append("Assistant: ").append(msg.getAiResponse()).append("\n\n");
        }

        sb.append("User: ").append(userMessage).append("\nAssistant:");
        return sb.toString();
    }

    private String parseResponse(String json) {
        if (json == null || json.isBlank()) {
            log.warn("Ollama: received empty response body");
            return AI_SERVICE_ERROR;
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode responseNode = root.path("response");

            if (responseNode.isMissingNode() || !responseNode.isTextual()) {
                log.warn("Ollama: 'response' field missing or not a string. Body: {}", json);
                return AI_SERVICE_ERROR;
            }

            String text = responseNode.asText().trim();
            if (text.isBlank()) {
                log.warn("Ollama: 'response' field is blank");
                return AI_SERVICE_ERROR;
            }

            log.info("Ollama: SUCCESS — replyLength={}", text.length());
            return text;

        } catch (Exception e) {
            log.error("Failed to parse Ollama response JSON: {}", e.getMessage(), e);
            return AI_SERVICE_ERROR;
        }
    }
}
