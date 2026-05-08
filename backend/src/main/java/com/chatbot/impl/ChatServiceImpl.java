package com.chatbot.impl;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.model.ChatSession;
import com.chatbot.model.Message;
import com.chatbot.model.User;
import com.chatbot.repository.ChatSessionRepository;
import com.chatbot.repository.MessageRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.AIService;
import com.chatbot.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.chatbot.constant.AppConstants.NEW_CHAT_TITLE;
import static com.chatbot.constant.AppConstants.NOT_AUTHENTICATED;
import static com.chatbot.constant.AppConstants.SESSION_NOT_FOUND;
import static com.chatbot.constant.AppConstants.USER_NOT_FOUND;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    public ChatServiceImpl(
            ChatSessionRepository chatSessionRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            AIService aiService) {
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
    }

    @Override
    @Transactional
    public ChatResponse chat(ChatRequest request) {
        User user = resolveCurrentUser();
        ChatSession session = resolveSession(user, request.getSessionId(), request.getMessage());

        // Load full conversation history so the AI has context for its reply.
        List<Message> history = messageRepository.findByChatSession_IdOrderByTimestampAsc(session.getId());

        String aiReply = aiService.chat(request.getMessage(), history);

        Message message = Message.builder()
                .chatSession(session)
                .userMessage(request.getMessage())
                .aiResponse(aiReply)
                .timestamp(Instant.now())
                .build();
        messageRepository.save(message);

        return ChatResponse.builder()
                .reply(aiReply)
                .sessionId(session.getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> listSessions() {
        User user = resolveCurrentUser();
        return chatSessionRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessages(Long sessionId) {
        User user = resolveCurrentUser();
        chatSessionRepository.findByIdAndUser_Id(sessionId, user.getId())
                .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND));
        return messageRepository.findByChatSession_IdOrderByTimestampAsc(sessionId);
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException(NOT_AUTHENTICATED);
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    }

    private ChatSession resolveSession(User user, Long sessionId, String firstMessage) {
        if (sessionId == null) {
            String title = buildTitle(firstMessage);
            ChatSession session = ChatSession.builder()
                    .user(user)
                    .title(title)
                    .createdAt(Instant.now())
                    .build();
            return chatSessionRepository.save(session);
        }

        return chatSessionRepository.findByIdAndUser_Id(sessionId, user.getId())
                .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND));
    }

    private String buildTitle(String message) {
        if (message == null || message.isBlank()) {
            return NEW_CHAT_TITLE;
        }
        String trimmed = message.trim().replaceAll("\\s+", " ");
        if (trimmed.length() <= 120) {
            return trimmed;
        }
        return trimmed.substring(0, 117) + "...";
    }
}
