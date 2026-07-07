package com.chatbot.dto;

import com.chatbot.model.FeedbackReason;
import com.chatbot.model.MessageFeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private String userMessage;
    private String aiResponse;
    private Instant timestamp;
    private String userBubbleClientId;
    private String assistantBubbleClientId;
    private boolean generationComplete;
    private MessageFeedbackType feedbackType;
    private FeedbackReason feedbackReason;
}
