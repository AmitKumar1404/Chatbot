package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * // NEW
 * Server → client streaming envelope. Every frame carries {@code clientStreamId} for stale-chunk filtering.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamDownstreamEvent {

    public static final String TYPE_CHUNK = "chunk";
    public static final String TYPE_DONE = "done";
    public static final String TYPE_ERROR = "error";

    private String clientStreamId;
    /** Assistant bubble id this stream updates (echoed for client-side routing). */
    private String assistantMessageId;
    private String type;
    private String chunk;
    private String message;

    public static StreamDownstreamEvent chunk(String clientStreamId, String assistantMessageId, String text) {
        StreamDownstreamEvent e = new StreamDownstreamEvent();
        e.clientStreamId = clientStreamId;
        e.assistantMessageId = assistantMessageId;
        e.type = TYPE_CHUNK;
        e.chunk = text;
        return e;
    }

    public static StreamDownstreamEvent done(String clientStreamId) {
        return done(clientStreamId, null);
    }

    public static StreamDownstreamEvent done(String clientStreamId, String assistantMessageId) {
        StreamDownstreamEvent e = new StreamDownstreamEvent();
        e.clientStreamId = clientStreamId;
        e.assistantMessageId = assistantMessageId;
        e.type = TYPE_DONE;
        return e;
    }

    public static StreamDownstreamEvent error(String clientStreamId, String assistantMessageId, String message) {
        StreamDownstreamEvent e = new StreamDownstreamEvent();
        e.clientStreamId = clientStreamId;
        e.assistantMessageId = assistantMessageId;
        e.type = TYPE_ERROR;
        e.message = message;
        return e;
    }

    public String getClientStreamId() {
        return clientStreamId;
    }

    public void setClientStreamId(String clientStreamId) {
        this.clientStreamId = clientStreamId;
    }

    public String getAssistantMessageId() {
        return assistantMessageId;
    }

    public void setAssistantMessageId(String assistantMessageId) {
        this.assistantMessageId = assistantMessageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
