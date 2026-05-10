package com.chatbot.service;

import com.chatbot.dto.PriorMessageDto;

import java.util.List;

/**
 * // NEW
 * Builds a single Ollama {@code prompt} string from optional prior turns plus the latest user message.
 */
public final class ChatPromptComposer {

    private ChatPromptComposer() {
    }

    public static String compose(List<PriorMessageDto> priorMessages, String latestUserContent) {
        StringBuilder sb = new StringBuilder();
        if (priorMessages != null) {
            for (PriorMessageDto m : priorMessages) {
                if (m == null || m.getRole() == null || m.getContent() == null) {
                    continue;
                }
                String role = m.getRole().trim();
                String content = m.getContent().trim();
                if (role.isEmpty() || content.isEmpty()) {
                    continue;
                }
                sb.append(role).append(": ").append(content).append('\n');
            }
        }
        sb.append("user: ").append(latestUserContent == null ? "" : latestUserContent.trim());
        return sb.toString();
    }
}
