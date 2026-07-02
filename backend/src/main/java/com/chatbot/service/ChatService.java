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

    List<Message> getMessages(Long sessionId, int page, int size);

    ChatSession createEmptySession();

    void deleteSession(Long sessionId);

    ChatSession updateSessionTitle(Long sessionId, String title);

    ChatSession updateSessionPinned(Long sessionId, boolean pinned);

    /**
     * Resolves an owned session for WebSocket streaming, or creates one when {@code sessionId} is null.
     */
    ChatSession resolveStreamingSessionForUser(String username, Long sessionId, String firstUserLineForTitle);

    /**
     * Creates or resets the DB row for an in-flight stream ({@code generationComplete=false}).
     */
    void beginStreamingTurn(
            String username,
            ChatStompPayload.Type type,
            Long sessionId,
            String userContent,
            String userMessageClientId,
            String assistantMessageClientId,
            String editTargetUserClientId
    );

    /**
     * Persists accumulated assistant text while the model is still streaming.
     */
    void updatePartialAiResponse(
            String username,
            Long sessionId,
            String assistantMessageClientId,
            String partialAssistantText
    );

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
