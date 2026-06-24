package com.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDto {

    private Long sessionId;
    private String sessionTitle;
    private String matchType;
    private Long messageId;
    private String content;
    private Instant timestamp;
}
