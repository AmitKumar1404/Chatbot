package com.chatbot.impl;

import com.chatbot.dto.FeedbackStatsResponse;
import com.chatbot.model.MessageFeedbackType;
import com.chatbot.repository.MessageFeedbackRepository;
import com.chatbot.service.FeedbackAnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackAnalyticsServiceImpl implements FeedbackAnalyticsService {

    private final MessageFeedbackRepository messageFeedbackRepository;

    public FeedbackAnalyticsServiceImpl(MessageFeedbackRepository messageFeedbackRepository) {
        this.messageFeedbackRepository = messageFeedbackRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackStatsResponse getStats() {
        long totalHelpful = messageFeedbackRepository.countByFeedbackType(MessageFeedbackType.HELPFUL);
        long totalNotHelpful = messageFeedbackRepository.countByFeedbackType(MessageFeedbackType.NOT_HELPFUL);
        return FeedbackStatsResponse.builder()
                .totalHelpful(totalHelpful)
                .totalNotHelpful(totalNotHelpful)
                .totalFeedback(totalHelpful + totalNotHelpful)
                .build();
    }
}
