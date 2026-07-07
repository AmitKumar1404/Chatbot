package com.chatbot.dto;

import com.chatbot.model.MessageFeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageFeedbackResponse {

    private Long messageId;
    private MessageFeedbackType feedbackType;
}
