package com.chatbot.service.rag;

import com.chatbot.model.DocumentChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ContextBuilderServiceImpl
        implements ContextBuilderService {

    private static final Logger log = LoggerFactory.getLogger(ContextBuilderServiceImpl.class);

    @Value("${app.rag.context.max-chunks:5}")
    private int maxChunks;

    @Value("${app.rag.context.max-characters:6000}")
    private int maxCharacters;

    @Override
    public String buildContext(List<DocumentChunk> chunks) {

        long startNano = System.nanoTime();
        Long documentId = resolveDocumentId(chunks);

        log.info("Context build started: documentId={}", documentId);

        if (chunks == null || chunks.isEmpty()) {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
            log.info(
                    "Context build completed: documentId={}, selectedChunks={}, totalCharacters={}, durationMs={}",
                    documentId,
                    0,
                    0,
                    durationMs
            );
            return "";
        }

        List<DocumentChunk> selected = new ArrayList<>();
        Set<String> seenNormalized = new HashSet<>();
        int totalCharacters = 0;

        for (DocumentChunk chunk : chunks) {

            if (selected.size() >= maxChunks) {
                break;
            }

            String content = chunk != null ? chunk.getContent() : null;
            if (content == null || content.isBlank()) {
                continue;
            }

            String normalized = normalizeForDedup(content);
            if (normalized.isEmpty() || !seenNormalized.add(normalized)) {
                continue;
            }

            int contentLength = content.length();
            if (totalCharacters + contentLength > maxCharacters) {
                break;
            }

            selected.add(chunk);
            totalCharacters += contentLength;
        }

        StringBuilder context = new StringBuilder();
        StringBuilder selectedIds = new StringBuilder();
        StringBuilder perChunkCounts = new StringBuilder();

        for (int i = 0; i < selected.size(); i++) {
            DocumentChunk chunk = selected.get(i);
            String content = chunk.getContent();

            context.append("Chunk ")
                    .append(i + 1)
                    .append(":\n");
            context.append(content);
            context.append("\n\n");

            if (i > 0) {
                selectedIds.append(',');
                perChunkCounts.append("; ");
            }
            selectedIds.append(chunk.getId());
            perChunkCounts.append("id=")
                    .append(chunk.getId())
                    .append(" chars=")
                    .append(content.length());
        }

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);

        log.info(
                "Context build completed: documentId={}, selectedChunks={}, totalCharacters={}, durationMs={}",
                documentId,
                selected.size(),
                totalCharacters,
                durationMs
        );

        if (log.isDebugEnabled()) {
            log.debug(
                    "Context build details: documentId={}, selectedChunkIds=[{}], perChunkChars=[{}]",
                    documentId,
                    selectedIds,
                    perChunkCounts
            );
        }

        return context.toString();
    }

    private Long resolveDocumentId(List<DocumentChunk> chunks) {

        if (chunks == null) {
            return null;
        }

        for (DocumentChunk chunk : chunks) {
            if (chunk != null && chunk.getDocument() != null) {
                return chunk.getDocument().getId();
            }
        }

        return null;
    }

    /**
     * Exact-duplicate key only: trim, lowercase, collapse consecutive whitespace.
     */
    private String normalizeForDedup(String text) {

        return text.trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }
}
