package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.FeedbackStatsResponse;
import com.chatbot.service.FeedbackAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/feedback")
public class FeedbackAnalyticsController {

    private final FeedbackAnalyticsService feedbackAnalyticsService;

    public FeedbackAnalyticsController(FeedbackAnalyticsService feedbackAnalyticsService) {
        this.feedbackAnalyticsService = feedbackAnalyticsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<FeedbackStatsResponse> stats() {
        return ResponseEntity.status(ResponseCode.OK).body(feedbackAnalyticsService.getStats());
    }
}
