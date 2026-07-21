package com.chatbot.service.embedding;

import java.util.List;

public interface EmbeddingService {

    List<Float> generateEmbedding(String text);

    List<Float> generateDocumentEmbedding(String text);

    /**
     * Batch document embeddings. Providers may implement true batching;
     * the default Ollama implementation loops internally.
     */
    List<List<Float>> generateDocumentEmbeddings(List<String> texts);

    List<Float> generateQueryEmbedding(String text);

}
