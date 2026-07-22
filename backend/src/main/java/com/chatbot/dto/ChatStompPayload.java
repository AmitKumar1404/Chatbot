package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * // NEW
 * Inbound STOMP payload for {@code /app/chat}. Supports normal sends and edit-regeneration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatStompPayload {

    public enum Type {
        NEW,
        EDIT
    }

    public enum ChatMode {
        NORMAL,
        DOCUMENT
    }

    /** // EDIT FEATURE — NEW vs regeneration */
    private Type type;

    /**
     * // NEW
     * Client-generated id for this stream; echoed on every downstream frame so the UI can drop stale chunks.
     */
    private String clientStreamId;

    /**
     * // EDIT FEATURE
     * Assistant bubble id that should receive streamed tokens (existing placeholder on NEW and EDIT).
     */
    private String messageId;

    /** User message text (new prompt or edited text). */
    private String content;

    /**
     * Server-side conversation id. When null, the server creates a new session (legacy clients).
     */
    private Long sessionId;

    private Long documentId;

    /**
     * Explicit chat mode from the client. {@code NORMAL} ignores any document;
     * {@code DOCUMENT} requires {@link #documentId} and uses the RAG pipeline.
     * When null/missing, the server treats the request as {@code NORMAL}.
     */
    private ChatMode chatMode;

    /**
     * Client id of the user bubble for {@link Type#NEW} (pairs with {@link #messageId} for the assistant).
     */
    private String userMessageId;

    /** // EDIT FEATURE — user bubble id being edited (EDIT only). */
    private String editTargetMessageId;

    /**
     * // NEW
     * Optional ordered history before the current user turn (excludes the assistant answer being regenerated).
     */
    private List<PriorMessageDto> priorMessages;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getClientStreamId() {
        return clientStreamId;
    }

    public void setClientStreamId(String clientStreamId) {
        this.clientStreamId = clientStreamId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getDocumentId(){
        return documentId;
    }

    public void setDocumentId(Long documentId){
        this.documentId = documentId;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    public String getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(String userMessageId) {
        this.userMessageId = userMessageId;
    }

    public String getEditTargetMessageId() {
        return editTargetMessageId;
    }

    public void setEditTargetMessageId(String editTargetMessageId) {
        this.editTargetMessageId = editTargetMessageId;
    }

    public List<PriorMessageDto> getPriorMessages() {
        return priorMessages;
    }

    public void setPriorMessages(List<PriorMessageDto> priorMessages) {
        this.priorMessages = priorMessages;
    }
}
