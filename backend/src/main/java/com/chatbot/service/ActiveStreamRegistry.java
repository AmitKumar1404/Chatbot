package com.chatbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class ActiveStreamRegistry {

    private static final Logger log = LoggerFactory.getLogger(ActiveStreamRegistry.class);

    private final ConcurrentMap<String, ActiveStream> activeStreams = new ConcurrentHashMap<>();

    /**
     * Cancels any existing stream then creates the next one inside the same
     * {@link ConcurrentHashMap#compute(java.lang.Object, java.util.function.BiFunction)} critical section.
     * <p>
     * This avoids the race where {@code Disposable.Swap} was disposed before
     * {@code update(streamSubscription)} ran, which left the Ollama pipeline subscribed
     * with no way for STOP to cancel it.
     */
    // UPDATED — carries clientStreamId for downstream DONE correlation on STOP
    public void replaceAndStart(String principalName, String streamId, String clientStreamId,
                                Long sessionId, String assistantMessageClientId,
                                Supplier<Disposable> subscribeSupplier) {
        activeStreams.compute(principalName, (key, existing) -> {
            if (existing != null) {
                log.warn("Replacing active stream — principal={}, oldStreamId={}, newStreamId={}",
                        principalName, existing.streamId(), streamId);
                existing.disposable().dispose();
            }
            Disposable d = Objects.requireNonNull(subscribeSupplier.get(), "subscribeSupplier");
            log.info("Stream started — principal={}, streamId={}, clientStreamId={}, sessionId={}",
                    principalName, streamId, clientStreamId, sessionId);
            return new ActiveStream(streamId, clientStreamId, sessionId, assistantMessageClientId, d);
        });
    }

    /**
     * Returns metadata for the user's active stream, if any (used after WebSocket reconnect).
     */
    public Optional<ActiveStreamStatus> getActiveStreamStatus(String principalName) {
        ActiveStream current = activeStreams.get(principalName);
        if (current == null) {
            return Optional.empty();
        }
        return Optional.of(new ActiveStreamStatus(
                current.clientStreamId(),
                current.sessionId(),
                current.assistantMessageClientId()
        ));
    }

    /**
     * Rejects a new /app/chat when the same clientStreamId is already running (duplicate recovery guard).
     */
    public boolean isDuplicateClientStream(String principalName, String clientStreamId) {
        ActiveStream current = activeStreams.get(principalName);
        return current != null
                && clientStreamId != null
                && clientStreamId.equals(current.clientStreamId());
    }

    /**
     * Cancels the active stream for one websocket principal.
     *
     * @return the client stream id that was active (for sending a matching DONE envelope).
     */
    // UPDATED — returns client stream id for UI correlation
    public Optional<String> cancel(String principalName, String reason) {
        ActiveStream removed = activeStreams.remove(principalName);
        if (removed == null) {
            log.info("No active stream to cancel — principal={}, reason={}", principalName, reason);
            return Optional.empty();
        }

        removed.disposable().dispose();
        log.info("Stream cancelled — principal={}, streamId={}, clientStreamId={}, reason={}",
                principalName, removed.streamId(), removed.clientStreamId(), reason);
        return Optional.ofNullable(removed.clientStreamId());
    }

    /**
     * Removes a stream only when the same streamId is still active.
     * This guards against race conditions with replacement streams.
     */
    public boolean removeIfMatches(String principalName, String streamId) {
        AtomicBoolean removed = new AtomicBoolean(false);
        activeStreams.computeIfPresent(principalName, (key, current) -> {
            if (current.streamId().equals(streamId)) {
                removed.set(true);
                return null;
            }
            return current;
        });
        return removed.get();
    }
    public boolean hasActiveStream(String userName) {
        return activeStreams.containsKey(userName);
    }

    public record ActiveStreamStatus(String clientStreamId, Long sessionId, String assistantMessageClientId) {
    }

    // UPDATED — ActiveStream tracks reconnect metadata alongside server stream id
    private record ActiveStream(
            String streamId,
            String clientStreamId,
            Long sessionId,
            String assistantMessageClientId,
            Disposable disposable
    ) {
    }
}
