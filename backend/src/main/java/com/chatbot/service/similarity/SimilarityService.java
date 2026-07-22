package com.chatbot.service.similarity;

import com.chatbot.model.DocumentChunk;

import java.util.List;

public interface SimilarityService {

    List<DocumentChunk> findRelevantChunks(
            String question,
            Long documentId,
            int topK
    );

}
