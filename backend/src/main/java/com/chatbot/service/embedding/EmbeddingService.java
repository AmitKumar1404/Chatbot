package com.chatbot.service.embedding;

import java.util.List;

public interface EmbeddingService {

    List<Float> generateEmbedding(String text);

    List<Float> generateDocumentEmbedding(String text);

    List<Float> generateQueryEmbedding(String text);

}
