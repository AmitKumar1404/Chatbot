package com.chatbot.service;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.dto.ChatStompPayload;
import com.chatbot.model.ChatSession;
import com.chatbot.model.Message;

import java.util.List;

public interface ChatService {

    ChatResponse chat(ChatRequest request);

    List<ChatSession> listSessions();

    List<Message> getMessages(Long sessionId);

    ChatSession createEmptySession();

    void deleteSession(Long sessionId);

    ChatSession updateSessionTitle(Long sessionId, String title);

    /**
     * Resolves an owned session for WebSocket streaming, or creates one when {@code sessionId} is null.
     */
    ChatSession resolveStreamingSessionForUser(String username, Long sessionId, String firstUserLineForTitle);

    /**
     * Persists a completed (or cancelled) streamed turn. Runs in a transaction suitable for reactive callbacks.
     */
    void persistWebsocketTurn(
            String username,
            ChatStompPayload.Type type,
            Long sessionId,
            String userContent,
            String userMessageClientId,
            String assistantMessageClientId,
            String editTargetUserClientId,
            String assistantText
    );
}
