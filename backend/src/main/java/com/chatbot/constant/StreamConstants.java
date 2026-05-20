package com.chatbot.constant;

/**
 * Centralized stream constants.
 * Avoids hardcoded magic strings across multiple classes.
 */
public final class StreamConstants {

    private StreamConstants() {
    }

    public static final String DONE = "[DONE]";

    public static final String ERROR_PREFIX = "[ERROR] ";

    /**
     * Transport/upstream interruptions (restart, disconnect, cancel) that must not
     * be surfaced as assistant message text.
     */
    public static boolean isTransientStreamingFailure(Throwable e) {
        if (e == null) {
            return false;
        }
        Throwable cur = e;
        while (cur != null) {
            if (cur instanceof java.util.concurrent.CancellationException) {
                return true;
            }
            String msg = cur.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("broken pipe")
                        || lower.contains("connection reset")
                        || lower.contains("connection closed")
                        || lower.contains("websocket")
                        || lower.contains("disconnect")
                        || lower.contains("connection refused")
                        || lower.contains("premature close")
                        || lower.contains("channel closed")
                        || lower.contains("socket closed")
                        || lower.contains("eof")
                        || lower.contains("timeout")) {
                    return true;
                }
            }
            cur = cur.getCause();
        }
        return false;
    }
}