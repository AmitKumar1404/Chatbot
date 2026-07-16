package com.chatbot.service.embedding;

import com.chatbot.dto.embedding.EmbeddingRequest;
import com.chatbot.dto.embedding.EmbeddingResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final String DOCUMENT_PREFIX = "search_document: ";
    private static final String QUERY_PREFIX = "search_query: ";

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
        return requestEmbedding(text);
    }

    @Override
    public List<Float> generateDocumentEmbedding(String text) {
        return requestEmbedding(DOCUMENT_PREFIX + text);
    }

    @Override
    public List<Float> generateQueryEmbedding(String text) {
        return requestEmbedding(QUERY_PREFIX + text);
    }

    private List<Float> requestEmbedding(String prompt) {

        System.out.println();
        System.out.println("========== EMBEDDING INPUT ==========");
        System.out.println(prompt);
        System.out.println("=====================================");

        EmbeddingRequest request =
                EmbeddingRequest.builder()
                        .model(embeddingModel)
                        .prompt(prompt)
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

