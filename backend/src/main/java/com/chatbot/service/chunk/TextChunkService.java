package com.chatbot.service.chunk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextChunkService {

    private static final int MIN_CHUNK_SIZE = 300;
    private static final int MAX_CHUNK_SIZE = 1000;

    private static final int LEGACY_CHUNK_SIZE = 450;
    private static final int LEGACY_CHUNK_OVERLAP = 100;

    private static final Pattern SENTENCE_BOUNDARY =
            Pattern.compile(".+?(?:[.!?]+(?=\\s|$)|$)", Pattern.DOTALL);

    private static final Pattern STRUCTURAL_HEADING = Pattern.compile(
            "^(?i)(?:section|chapter|article|appendix|part)\\b(?:\\s+[\\dIVXLCDM]+)?(?:\\s*[:.\\-–—]?\\s*.*)?$"
    );

    private static final Pattern NUMBERED_HEADING = Pattern.compile(
            "^\\d+(?:\\.\\d+)+\\.?\\s+\\S.+$"
    );

    private static final Pattern NUMBERED_SECTION_HEADING = Pattern.compile(
            "^(?i)\\d+\\.\\s+[A-Z].+$"
    );

    private static final Pattern LIST_ITEM = Pattern.compile(
            "^(?:[•●○▪▫*]\\s+|[-–—]\\s+|\\d+[.)]\\s+|[a-zA-Z][.)]\\s+).+"
    );

    @Value("${app.rag.chunking.enabled:true}")
    private boolean chunkingEnabled;

    @Value("${app.rag.chunking.chunk-size:800}")
    private int configuredChunkSize;

    @Value("${app.rag.chunking.overlap:100}")
    private int configuredOverlap;

    /**
     * Returns chunks with metadata. Only content + chunkIndex are persisted today;
     * other fields are ready for a future DB migration without changing this algorithm.
     */
    public List<TextChunk> chunk(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        if (!chunkingEnabled) {
            return legacyFixedSizeChunks(text.trim());
        }

        return paragraphAwareChunks(text);
    }

    /** Backward-compatible helper. */
    public List<String> chunkText(String text) {

        List<TextChunk> chunks = chunk(text);
        List<String> contents = new ArrayList<>(chunks.size());
        for (TextChunk chunk : chunks) {
            contents.add(chunk.getContent());
        }
        return contents;
    }

    private List<TextChunk> paragraphAwareChunks(String rawText) {

        int targetSize = clamp(configuredChunkSize, MIN_CHUNK_SIZE, MAX_CHUNK_SIZE);
        int overlap = Math.max(0, Math.min(configuredOverlap, targetSize / 2));

        String normalized = normalizeWhitespace(rawText);
        List<Segment> segments = buildSegments(normalized, targetSize);
        return packSegments(segments, normalized, targetSize, overlap);
    }

    private String normalizeWhitespace(String text) {

        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        normalized = normalized.replaceAll("[ \\t]+", " ");
        normalized = normalized.replaceAll(" *\\n *", "\n");
        normalized = normalized.replaceAll("\\n{3,}", "\n\n");
        return normalized.trim();
    }

    /**
     * Build ordered content segments with heading metadata.
     * Heading lines are not emitted alone; they attach to the following body.
     * Paragraphs under different headings are never merged.
     */
    private List<Segment> buildSegments(String text, int targetSize) {

        String[] rawParagraphs = text.split("\\n\\s*\\n");
        List<Segment> segments = new ArrayList<>();

        String currentHeading = null;
        int searchFrom = 0;
        StringBuilder mergeBuffer = new StringBuilder();
        String mergeHeading = null;
        int mergeStart = -1;

        for (String raw : rawParagraphs) {

            String paragraph = raw.trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            int start = indexOfFrom(text, paragraph, searchFrom);
            int end = start + paragraph.length();
            searchFrom = end;

            if (isHeading(paragraph)) {
                flushMergeBuffer(segments, mergeBuffer, mergeHeading, mergeStart, text);
                mergeBuffer.setLength(0);
                mergeStart = -1;
                mergeHeading = null;
                currentHeading = collapseWhitespace(paragraph);
                continue;
            }

            if (paragraph.length() < MIN_CHUNK_SIZE) {

                // Never merge across different section headings.
                if (mergeBuffer.length() > 0 && !Objects.equals(mergeHeading, currentHeading)) {
                    flushMergeBuffer(segments, mergeBuffer, mergeHeading, mergeStart, text);
                    mergeBuffer.setLength(0);
                    mergeStart = -1;
                    mergeHeading = null;
                }

                if (mergeBuffer.length() == 0) {
                    mergeStart = start;
                    mergeHeading = currentHeading;
                } else {
                    mergeBuffer.append("\n\n");
                }
                mergeBuffer.append(paragraph);

                if (mergeBuffer.length() >= MIN_CHUNK_SIZE) {
                    flushMergeBuffer(segments, mergeBuffer, mergeHeading, mergeStart, text);
                    mergeBuffer.setLength(0);
                    mergeStart = -1;
                    mergeHeading = null;
                }
                continue;
            }

            flushMergeBuffer(segments, mergeBuffer, mergeHeading, mergeStart, text);
            mergeBuffer.setLength(0);
            mergeStart = -1;
            mergeHeading = null;

            if (paragraph.length() <= MAX_CHUNK_SIZE) {
                segments.add(new Segment(paragraph, start, end, currentHeading));
            } else {
                segments.addAll(splitOversized(paragraph, start, currentHeading, targetSize));
            }
        }

        flushMergeBuffer(segments, mergeBuffer, mergeHeading, mergeStart, text);
        return segments;
    }

    private void flushMergeBuffer(
            List<Segment> segments,
            StringBuilder mergeBuffer,
            String heading,
            int mergeStart,
            String fullText) {

        if (mergeBuffer.length() == 0) {
            return;
        }

        String content = mergeBuffer.toString();
        int start = mergeStart >= 0 ? mergeStart : indexOfFrom(fullText, content, 0);
        int end = Math.min(start + content.length(), fullText.length());
        segments.add(new Segment(content, start, end, heading));
    }

    private List<Segment> splitOversized(
            String paragraph,
            int paragraphStart,
            String heading,
            int targetSize) {

        List<String> units = splitIntoUnits(paragraph);
        List<Segment> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int bufferStartOffset = 0;
        int cursor = 0;

        for (String unit : units) {

            String trimmed = unit.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            int unitOffset = paragraph.indexOf(trimmed, cursor);
            if (unitOffset < 0) {
                unitOffset = cursor;
            }

            if (trimmed.length() > MAX_CHUNK_SIZE) {
                if (buffer.length() > 0) {
                    parts.add(segmentFromBuffer(buffer, paragraphStart, bufferStartOffset, heading));
                    buffer.setLength(0);
                }
                for (String hard : hardSplit(trimmed, MAX_CHUNK_SIZE)) {
                    int hardOffset = paragraph.indexOf(hard, unitOffset);
                    if (hardOffset < 0) {
                        hardOffset = unitOffset;
                    }
                    parts.add(new Segment(
                            hard,
                            paragraphStart + hardOffset,
                            paragraphStart + hardOffset + hard.length(),
                            heading
                    ));
                    unitOffset = hardOffset + hard.length();
                }
                cursor = unitOffset;
                continue;
            }

            String separator = chooseSeparator(buffer, trimmed);
            int extra = buffer.length() == 0
                    ? trimmed.length()
                    : trimmed.length() + separator.length();

            if (buffer.length() > 0 && buffer.length() + extra > targetSize) {
                parts.add(segmentFromBuffer(buffer, paragraphStart, bufferStartOffset, heading));
                buffer.setLength(0);
            }

            if (buffer.length() == 0) {
                bufferStartOffset = unitOffset;
            } else {
                buffer.append(separator);
            }
            buffer.append(trimmed);
            cursor = unitOffset + trimmed.length();
        }

        if (buffer.length() > 0) {
            parts.add(segmentFromBuffer(buffer, paragraphStart, bufferStartOffset, heading));
        }

        return parts;
    }

    private String chooseSeparator(StringBuilder buffer, String nextUnit) {

        if (buffer.length() == 0) {
            return "";
        }
        if (isListItem(nextUnit) || endsWithListItem(buffer.toString())) {
            return "\n";
        }
        return " ";
    }

    private boolean endsWithListItem(String text) {

        String[] lines = text.split("\\n");
        if (lines.length == 0) {
            return false;
        }
        return isListItem(lines[lines.length - 1].trim());
    }

    private Segment segmentFromBuffer(
            StringBuilder buffer,
            int paragraphStart,
            int bufferStartOffset,
            String heading) {

        String content = buffer.toString().trim();
        int start = paragraphStart + bufferStartOffset;
        return new Segment(content, start, start + content.length(), heading);
    }

    /**
     * Pack segments into chunks. Offsets always come from original document positions.
     * Overlap is taken as a document substring ending at the previous chunk's characterEnd.
     */
    private List<TextChunk> packSegments(
            List<Segment> segments,
            String fullText,
            int targetSize,
            int overlap) {

        List<TextChunk> chunks = new ArrayList<>();
        if (segments.isEmpty()) {
            return chunks;
        }

        List<Segment> buffer = new ArrayList<>();

        for (Segment segment : segments) {

            if (buffer.isEmpty()) {
                buffer.add(segment);
                continue;
            }

            // Keep different headings in separate chunks when packing.
            if (!Objects.equals(buffer.get(0).sectionHeading, segment.sectionHeading)) {
                chunks.add(emitPackedChunk(chunks.size(), buffer, fullText));
                buffer = new ArrayList<>();
                buffer.add(segment);
                continue;
            }

            int joinedLength = packedContentLength(buffer) + 2 + segment.content.length();

            if (joinedLength <= targetSize) {
                buffer.add(segment);
                continue;
            }

            TextChunk emitted = emitPackedChunk(chunks.size(), buffer, fullText);
            chunks.add(emitted);

            int overlapStart = resolveOverlapStart(fullText, emitted.getCharacterStart(), emitted.getCharacterEnd(), overlap);
            buffer = new ArrayList<>();

            if (overlapStart >= 0 && overlapStart < emitted.getCharacterEnd()) {
                String overlapContent = fullText.substring(overlapStart, emitted.getCharacterEnd()).trim();
                if (!overlapContent.isEmpty()) {
                    buffer.add(new Segment(
                            overlapContent,
                            overlapStart,
                            emitted.getCharacterEnd(),
                            segment.sectionHeading
                    ));
                }
            }

            buffer.add(segment);
        }

        if (!buffer.isEmpty()) {
            chunks.add(emitPackedChunk(chunks.size(), buffer, fullText));
        }

        return chunks;
    }

    private int packedContentLength(List<Segment> buffer) {

        int length = 0;
        for (int i = 0; i < buffer.size(); i++) {
            if (i > 0) {
                length += 2;
            }
            length += buffer.get(i).content.length();
        }
        return length;
    }

    private TextChunk emitPackedChunk(
            int index,
            List<Segment> buffer,
            String fullText) {

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < buffer.size(); i++) {
            if (i > 0) {
                content.append("\n\n");
            }
            content.append(buffer.get(i).content);
        }

        int characterStart = buffer.get(0).start;
        int characterEnd = buffer.get(buffer.size() - 1).end;
        characterStart = clamp(characterStart, 0, fullText.length());
        characterEnd = clamp(characterEnd, characterStart, fullText.length());

        String sectionHeading = null;
        for (Segment segment : buffer) {
            if (segment.sectionHeading != null) {
                sectionHeading = segment.sectionHeading;
                break;
            }
        }

        String trimmed = content.toString().trim();

        return TextChunk.builder()
                .content(trimmed)
                .chunkIndex(index)
                .characterStart(characterStart)
                .characterEnd(characterEnd)
                .sectionHeading(sectionHeading)
                .wordCount(countWords(trimmed))
                .estimatedPageNumber(estimatePageNumber(fullText, characterStart))
                .build();
    }

    /**
     * Choose an overlap window ending at previousEnd that prefers a sentence/list boundary
     * and stays within the original document span.
     */
    private int resolveOverlapStart(
            String fullText,
            int previousStart,
            int previousEnd,
            int overlap) {

        if (overlap <= 0 || previousEnd <= previousStart) {
            return -1;
        }

        int maxStart = Math.max(previousStart, previousEnd - overlap);
        String window = fullText.substring(maxStart, previousEnd);

        List<String> units = splitIntoUnits(window);
        if (!units.isEmpty()) {
            String lastUnit = units.get(units.size() - 1).trim();
            if (!lastUnit.isEmpty() && lastUnit.length() <= overlap) {
                int idx = window.lastIndexOf(lastUnit);
                if (idx >= 0) {
                    return maxStart + idx;
                }
            }
        }

        int space = window.indexOf(' ');
        if (space >= 0 && space < window.length() - 1) {
            return maxStart + space + 1;
        }

        return maxStart;
    }

    /**
     * Split text into retrieval-friendly units:
     * list items, sentences, and semicolon-separated clauses when appropriate.
     */
    private List<String> splitIntoUnits(String text) {

        List<String> units = new ArrayList<>();
        String[] lines = text.split("\\n");

        boolean hasListStructure = false;
        for (String line : lines) {
            if (isListItem(line.trim())) {
                hasListStructure = true;
                break;
            }
        }

        if (hasListStructure && lines.length > 1) {
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (isListItem(trimmed)) {
                    units.add(trimmed);
                } else {
                    units.addAll(splitBySentenceAndSemicolon(trimmed));
                }
            }
            return units;
        }

        return splitBySentenceAndSemicolon(text);
    }

    private List<String> splitBySentenceAndSemicolon(String text) {

        List<String> sentences = new ArrayList<>();
        Matcher matcher = SENTENCE_BOUNDARY.matcher(text);

        while (matcher.find()) {
            String sentence = matcher.group().trim();
            if (!sentence.isEmpty()) {
                sentences.addAll(splitSemicolonClauses(sentence));
            }
        }

        if (sentences.isEmpty() && !text.isBlank()) {
            sentences.addAll(splitSemicolonClauses(text.trim()));
        }

        return sentences;
    }

    private List<String> splitSemicolonClauses(String text) {

        if (!text.contains(";")) {
            return List.of(text);
        }

        String[] parts = text.split(";\\s*");
        if (parts.length < 2) {
            return List.of(text);
        }

        // Only split when clauses look like separate items, not a single dense sentence.
        int substantial = 0;
        for (String part : parts) {
            if (part.trim().length() >= 12) {
                substantial++;
            }
        }

        if (substantial < 2) {
            return List.of(text);
        }

        List<String> clauses = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) {
                continue;
            }
            if (i < parts.length - 1 && !part.endsWith(".") && !part.endsWith("?") && !part.endsWith("!")) {
                part = part + ";";
            }
            clauses.add(part);
        }

        return clauses.isEmpty() ? List.of(text) : clauses;
    }

    private boolean isListItem(String line) {

        if (line == null || line.isBlank()) {
            return false;
        }
        return LIST_ITEM.matcher(line).matches();
    }

    private List<String> hardSplit(String text, int maxSize) {

        List<String> parts = new ArrayList<>();
        for (int start = 0; start < text.length(); start += maxSize) {
            parts.add(text.substring(start, Math.min(start + maxSize, text.length())));
        }
        return parts;
    }

    private boolean isHeading(String paragraph) {

        String collapsed = collapseWhitespace(paragraph);
        if (collapsed.isEmpty() || collapsed.length() > 100) {
            return false;
        }
        if (collapsed.contains("\n")) {
            return false;
        }
        if (collapsed.endsWith(",") || collapsed.endsWith(";")) {
            return false;
        }

        // Structural document headings: SECTION 1, CHAPTER 2, ARTICLE IV, APPENDIX, PART A
        if (STRUCTURAL_HEADING.matcher(collapsed).matches()) {
            return true;
        }

        // Numbered headings: 2.1 Introduction, 3.2.1 Details
        if (NUMBERED_HEADING.matcher(collapsed).matches()) {
            return true;
        }

        // Simple numbered section: 1. Introduction
        if (NUMBERED_SECTION_HEADING.matcher(collapsed).matches() && countWords(collapsed) <= 12) {
            return true;
        }

        if (collapsed.endsWith(".")) {
            return false;
        }

        String lettersOnly = collapsed.replaceAll("[^A-Za-z]", "");
        if (lettersOnly.length() < 3) {
            return false;
        }

        int words = countWords(collapsed);
        boolean allCaps = lettersOnly.equals(lettersOnly.toUpperCase()) && words <= 10;
        boolean titleCase = isTitleCase(collapsed) && words <= 8;

        return allCaps || titleCase;
    }

    private boolean isTitleCase(String text) {

        String[] words = text.split("\\s+");
        int significant = 0;
        int titled = 0;

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            // Ignore short connector words for title-case detection.
            if (word.equalsIgnoreCase("a")
                    || word.equalsIgnoreCase("an")
                    || word.equalsIgnoreCase("the")
                    || word.equalsIgnoreCase("of")
                    || word.equalsIgnoreCase("and")
                    || word.equalsIgnoreCase("or")
                    || word.equalsIgnoreCase("to")
                    || word.equalsIgnoreCase("in")
                    || word.equalsIgnoreCase("on")
                    || word.equalsIgnoreCase("for")) {
                continue;
            }
            significant++;
            if (Character.isLetter(word.charAt(0)) && Character.isUpperCase(word.charAt(0))) {
                titled++;
            }
        }

        return significant > 0 && titled == significant;
    }

    private Integer estimatePageNumber(String fullText, int offset) {

        if (!fullText.contains("\f") || offset < 0) {
            return null;
        }

        int formFeeds = 0;
        int limit = Math.min(offset, fullText.length());
        for (int i = 0; i < limit; i++) {
            if (fullText.charAt(i) == '\f') {
                formFeeds++;
            }
        }
        return formFeeds + 1;
    }

    private int countWords(String text) {

        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    private String collapseWhitespace(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    private int indexOfFrom(String haystack, String needle, int from) {

        int index = haystack.indexOf(needle, from);
        return index >= 0 ? index : Math.max(0, from);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private List<TextChunk> legacyFixedSizeChunks(String text) {

        List<TextChunk> chunks = new ArrayList<>();
        int step = LEGACY_CHUNK_SIZE - LEGACY_CHUNK_OVERLAP;

        for (int start = 0; start < text.length(); start += step) {

            int end = Math.min(start + LEGACY_CHUNK_SIZE, text.length());
            String content = text.substring(start, end);

            chunks.add(
                    TextChunk.builder()
                            .content(content)
                            .chunkIndex(chunks.size())
                            .characterStart(start)
                            .characterEnd(end)
                            .sectionHeading(null)
                            .wordCount(countWords(content))
                            .estimatedPageNumber(estimatePageNumber(text, start))
                            .build()
            );

            if (end == text.length()) {
                break;
            }
        }

        return chunks;
    }

    private static final class Segment {

        private final String content;
        private final int start;
        private final int end;
        private final String sectionHeading;

        private Segment(String content, int start, int end, String sectionHeading) {
            this.content = content;
            this.start = start;
            this.end = end;
            this.sectionHeading = sectionHeading;
        }
    }
}
