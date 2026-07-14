package com.chatbot.service.embedding;

import com.chatbot.dto.embedding.EmbeddingRequest;
import com.chatbot.dto.embedding.EmbeddingResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.chatbot.service.embedding.EmbeddingService;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    @Value("${app.ai.ollama.base-url}")
    private String baseUrl;

    @Value("${app.ai.ollama.embedding-model}")
    private String embeddingModel;

    private final RestTemplate restTemplate;

    public EmbeddingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Float> generateEmbedding(String text) {

        EmbeddingRequest request =
                EmbeddingRequest.builder()
                        .model(embeddingModel)
                        .prompt(text)
                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmbeddingRequest> entity =
                new HttpEntity<>(request, headers);

        EmbeddingResponse response =
                restTemplate.postForObject(
                        baseUrl + "/api/embeddings",
                        entity,
                        EmbeddingResponse.class
                );

        if (response == null || response.getEmbedding() == null) {

            throw new RuntimeException(
                    "Failed to generate embedding from Ollama."
            );
        }

        return response.getEmbedding();
    }
}