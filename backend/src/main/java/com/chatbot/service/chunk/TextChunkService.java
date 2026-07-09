package com.chatbot.service.chunk;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkService {

    private static final int CHUNK_SIZE = 1000;

    public List<String> chunkText(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        text = text.trim();

        for (int start = 0; start < text.length(); start += CHUNK_SIZE) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            chunks.add(text.substring(start, end));
        }

        return chunks;
    }
}