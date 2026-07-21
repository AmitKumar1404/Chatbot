package com.chatbot.service.chunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * In-memory chunk with metadata. Only {@code content} and {@code chunkIndex}
 * are persisted today; remaining fields are ready for a future DB migration.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextChunk {

    private String content;

    private int chunkIndex;

    private int characterStart;

    private int characterEnd;

    private String sectionHeading;

    private int wordCount;

    private Integer estimatedPageNumber;
}
