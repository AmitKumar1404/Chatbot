package com.chatbot.service;

import com.chatbot.dto.MessageFeedbackRequest;
import com.chatbot.dto.MessageFeedbackResponse;

public interface MessageFeedbackService {

    MessageFeedbackResponse upsertFeedback(Long messageId, MessageFeedbackRequest request);
}
