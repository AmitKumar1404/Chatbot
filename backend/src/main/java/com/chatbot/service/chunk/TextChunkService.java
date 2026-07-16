package com.chatbot.service.chunk;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkService {

    private static final int CHUNK_SIZE = 450;
    private static final int CHUNK_OVERLAP = 100;

    public List<String> chunkText(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        text = text.trim();

        int step = CHUNK_SIZE - CHUNK_OVERLAP;

        for (int start = 0; start < text.length(); start += step) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            chunks.add(text.substring(start, end));

            if (end == text.length()) {
                break;
            }
        }

        return chunks;
    }
}
