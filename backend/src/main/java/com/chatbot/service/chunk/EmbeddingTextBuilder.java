package com.chatbot.service.chunk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Builds the text sent to the embedding model.
 * Stored chunk content remains unchanged; only the embedding input may be enriched.
 */
@Component
public class EmbeddingTextBuilder {

    @Value("${app.rag.embedding.include-heading:true}")
    private boolean includeHeading;

    @Value("${app.rag.embedding.include-page-number:false}")
    private boolean includePageNumber;

    @Value("${app.rag.embedding.include-metadata:false}")
    private boolean includeMetadata;

    /**
     * Derive embedding input from {@link TextChunk} metadata + content.
     * Phase 3 uses heading only when {@code include-heading} is true.
     * Page/metadata flags are reserved for future use.
     */
    public String build(TextChunk chunk) {

        if (chunk == null || chunk.getContent() == null) {
            return "";
        }

        String content = chunk.getContent();
        StringBuilder embeddingInput = new StringBuilder();

        if (includeHeading) {
            String heading = chunk.getSectionHeading();
            if (heading != null && !heading.isBlank()) {
                embeddingInput.append(heading.trim());
                embeddingInput.append("\n\n");
            }
        }

        // Reserved for future phases — intentionally unused today.
        if (includePageNumber && chunk.getEstimatedPageNumber() != null) {
            // no-op in Phase 3
        }
        if (includeMetadata) {
            // no-op in Phase 3
        }

        embeddingInput.append(content);
        return embeddingInput.toString();
    }
}
