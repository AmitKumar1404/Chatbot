package com.chatbot.dto;

import com.chatbot.model.MessageFeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageFeedbackRequest {

    @NotNull
    private MessageFeedbackType feedbackType;
}
