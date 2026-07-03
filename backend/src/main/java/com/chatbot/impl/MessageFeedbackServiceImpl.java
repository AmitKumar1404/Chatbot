package com.chatbot.impl;

import com.chatbot.dto.MessageFeedbackRequest;
import com.chatbot.dto.MessageFeedbackResponse;
import com.chatbot.model.Message;
import com.chatbot.model.MessageFeedback;
import com.chatbot.model.MessageFeedbackType;
import com.chatbot.model.User;
import com.chatbot.repository.MessageFeedbackRepository;
import com.chatbot.repository.MessageRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.MessageFeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.chatbot.constant.AppConstants.NOT_AUTHENTICATED;
import static com.chatbot.constant.AppConstants.USER_NOT_FOUND;

@Service
public class MessageFeedbackServiceImpl implements MessageFeedbackService {

    private static final String MESSAGE_NOT_FOUND = "Message not found";
    private static final String MESSAGE_NOT_OWNED = "Message does not belong to current user";
    private static final String MESSAGE_INCOMPLETE = "Cannot provide feedback before message generation is complete";

    private final MessageFeedbackRepository messageFeedbackRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageFeedbackServiceImpl(
            MessageFeedbackRepository messageFeedbackRepository,
            MessageRepository messageRepository,
            UserRepository userRepository) {
        this.messageFeedbackRepository = messageFeedbackRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MessageFeedbackResponse upsertFeedback(Long messageId, MessageFeedbackRequest request) {
        User user = resolveCurrentUser();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException(MESSAGE_NOT_FOUND));

        Long ownerId = message.getChatSession().getUser().getId();
        if (!user.getId().equals(ownerId)) {
            throw new RuntimeException(MESSAGE_NOT_OWNED);
        }
        if (!message.isGenerationComplete()) {
            throw new RuntimeException(MESSAGE_INCOMPLETE);
        }

        MessageFeedbackType feedbackType = request.getFeedbackType();
        Instant now = Instant.now();

        MessageFeedback feedback = messageFeedbackRepository
                .findByMessage_IdAndUser_Id(messageId, user.getId())
                .map(existing -> {
                    existing.setFeedbackType(feedbackType);
                    existing.setUpdatedAt(now);
                    return existing;
                })
                .orElseGet(() -> MessageFeedback.builder()
                        .message(message)
                        .user(user)
                        .feedbackType(feedbackType)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        MessageFeedback saved = messageFeedbackRepository.save(feedback);
        return MessageFeedbackResponse.builder()
                .messageId(saved.getMessage().getId())
                .feedbackType(saved.getFeedbackType())
                .build();
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException(NOT_AUTHENTICATED);
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    }
}
