package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * // NEW
 * One turn of conversation context sent from the client for multi-turn prompts.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriorMessageDto {

    private String role;
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
