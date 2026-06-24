package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Exposes the in-flight stream for the authenticated user (reconnect recovery).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveStreamStatusDto {

    private String clientStreamId;
    private Long sessionId;
    private String assistantMessageId;

    public ActiveStreamStatusDto() {
    }

    public ActiveStreamStatusDto(String clientStreamId, Long sessionId, String assistantMessageId) {
        this.clientStreamId = clientStreamId;
        this.sessionId = sessionId;
        this.assistantMessageId = assistantMessageId;
    }

    public String getClientStreamId() {
        return clientStreamId;
    }

    public void setClientStreamId(String clientStreamId) {
        this.clientStreamId = clientStreamId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getAssistantMessageId() {
        return assistantMessageId;
    }

    public void setAssistantMessageId(String assistantMessageId) {
        this.assistantMessageId = assistantMessageId;
    }
}
