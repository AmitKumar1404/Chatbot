package com.chatbot.impl;

import com.chatbot.dto.SearchResultDto;
import com.chatbot.model.ChatSession;
import com.chatbot.model.Message;
import com.chatbot.model.User;
import com.chatbot.repository.ChatSessionRepository;
import com.chatbot.repository.MessageRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.chatbot.constant.AppConstants.NOT_AUTHENTICATED;
import static com.chatbot.constant.AppConstants.USER_NOT_FOUND;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);
    private static final int CONTENT_PREVIEW_LIMIT = 240;
    private static final String TITLE_MATCH_TYPE = "TITLE";
    private static final String MESSAGE_MATCH_TYPE = "MESSAGE";

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public SearchServiceImpl(ChatSessionRepository chatSessionRepository,
                             MessageRepository messageRepository,
                             UserRepository userRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchResultDto> search(String query) {
        String keyword = query == null ? "" : query.trim();
        log.info("Search request received. incomingQuery='{}', normalizedKeyword='{}'", query, keyword);
        if (keyword.isEmpty()) {
            log.info("Search skipped because normalized keyword is empty");
            return List.of();
        }

        User user = resolveCurrentUser();
        log.info("Search executing for userId={}", user.getId());
        List<SearchResultDto> results = new ArrayList<>();

        List<ChatSession> titleMatches = chatSessionRepository
                .findByUser_IdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(user.getId(), keyword);
        log.info("Title query result size={}", titleMatches.size());
        for (ChatSession session : titleMatches) {
            results.add(SearchResultDto.builder()
                    .sessionId(session.getId())
                    .sessionTitle(session.getTitle())
                    .matchType(TITLE_MATCH_TYPE)
                    .content(session.getTitle())
                    .timestamp(session.getCreatedAt())
                    .build());
        }

        List<Message> messageMatches = messageRepository.searchByUserAndContent(user.getId(), keyword);
        log.info("Message query result size={}", messageMatches.size());
        for (Message message : messageMatches) {
            ChatSession session = message.getChatSession();
            results.add(SearchResultDto.builder()
                    .sessionId(session.getId())
                    .sessionTitle(session.getTitle())
                    .matchType(MESSAGE_MATCH_TYPE)
                    .messageId(message.getId())
                    .content(contentPreview(message))
                    .timestamp(message.getTimestamp())
                    .build());
        }

        log.info("Search total result size={}", results.size());
        return results;
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException(NOT_AUTHENTICATED);
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    }

    private String contentPreview(Message message) {
        String userMessage = message.getUserMessage();
        String aiResponse = message.getAiResponse();
        String merged = (userMessage == null ? "" : userMessage) + "\n" + (aiResponse == null ? "" : aiResponse);
        String normalized = merged.trim().replaceAll("\\s+", " ");
        if (normalized.length() <= CONTENT_PREVIEW_LIMIT) {
            return normalized;
        }
        return normalized.substring(0, CONTENT_PREVIEW_LIMIT - 3) + "...";
    }
}
