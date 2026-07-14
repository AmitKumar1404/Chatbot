package com.chatbot.dto.similarity;

import com.chatbot.model.DocumentChunk;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilarityResult {

    private DocumentChunk chunk;

    private double score;
}