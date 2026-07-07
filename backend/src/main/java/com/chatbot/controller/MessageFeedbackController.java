package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.MessageFeedbackRequest;
import com.chatbot.dto.MessageFeedbackResponse;
import com.chatbot.service.MessageFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chatbot.constant.AppConstants.CHAT_BASE_PATH;

@RestController
@RequestMapping(CHAT_BASE_PATH)
public class MessageFeedbackController {

    private final MessageFeedbackService messageFeedbackService;

    public MessageFeedbackController(MessageFeedbackService messageFeedbackService) {
        this.messageFeedbackService = messageFeedbackService;
    }

    @PutMapping("/messages/{messageId}/feedback")
    public ResponseEntity<MessageFeedbackResponse> upsertFeedback(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageFeedbackRequest request) {
        return ResponseEntity.status(ResponseCode.OK)
                .body(messageFeedbackService.upsertFeedback(messageId, request));
    }
}
