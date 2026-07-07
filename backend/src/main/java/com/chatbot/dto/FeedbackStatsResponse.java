package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackStatsResponse {

    private long totalHelpful;
    private long totalNotHelpful;
    private long totalFeedback;
}
