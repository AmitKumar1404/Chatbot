package com.chatbot.service.embedding;

import com.chatbot.dto.embedding.EmbeddingRequest;
import com.chatbot.dto.embedding.EmbeddingResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingServiceImpl.class);

    private static final String DOCUMENT_PREFIX = "search_document: ";
    private static final String QUERY_PREFIX = "search_query: ";

    @Value("${app.ai.ollama.base-url}")
    private String baseUrl;

    @Value("${app.ai.ollama.embedding-model}")
    private String embeddingModel;

    @Value("${app.rag.embedding.batch-size:32}")
    private int batchSize;

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

    /**
     * Generates embeddings for multiple document texts.
     * <p>
     * The current Ollama implementation processes texts sequentially within
     * configurable batch windows ({@code app.rag.embedding.batch-size}).
     * It does not issue true batch HTTP embedding requests. The API shape is
     * future-ready for providers that support native batch embedding.
     */
    @Override
    public List<List<Float>> generateDocumentEmbeddings(List<String> texts) {

        if (texts == null || texts.isEmpty()) {
            return List.of();
        }

        int effectiveBatchSize = Math.max(1, batchSize);
        List<List<Float>> embeddings = new ArrayList<>(texts.size());

        for (int start = 0; start < texts.size(); start += effectiveBatchSize) {

            int end = Math.min(start + effectiveBatchSize, texts.size());

            for (int i = start; i < end; i++) {
                try {
                    embeddings.add(generateDocumentEmbedding(texts.get(i)));
                } catch (RuntimeException ex) {
                    log.warn("Failed to generate embedding for one document text: {}",
                            ex.getMessage());
                    embeddings.add(null);
                }
            }
        }

        return embeddings;
    }

    @Override
    public List<Float> generateQueryEmbedding(String text) {
        return requestEmbedding(QUERY_PREFIX + text);
    }

    private List<Float> requestEmbedding(String prompt) {

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
