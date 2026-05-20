package com.chatbot.controller;

import com.chatbot.dto.ChatStompPayload;
import com.chatbot.dto.StreamDownstreamEvent;
import com.chatbot.model.ChatSession;
import com.chatbot.service.ActiveStreamRegistry;
import com.chatbot.service.ChatPromptComposer;
import com.chatbot.service.ChatService;
import com.chatbot.service.OllamaStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.SignalType;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.chatbot.constant.StreamConstants.ERROR_PREFIX;
import static com.chatbot.constant.StreamConstants.isTransientStreamingFailure;

@Controller
public class ChatWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);
    private static final String USER_QUEUE = "/queue/messages";
    private static final long PARTIAL_PERSIST_INTERVAL_MS = 400;

    private final OllamaStreamingService streamingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveStreamRegistry activeStreamRegistry;
    private final ChatService chatService;

    public ChatWebSocketController(OllamaStreamingService streamingService,
                                   SimpMessagingTemplate messagingTemplate,
                                   ActiveStreamRegistry activeStreamRegistry,
                                   ChatService chatService) {
        this.streamingService = streamingService;
        this.messagingTemplate = messagingTemplate;
        this.activeStreamRegistry = activeStreamRegistry;
        this.chatService = chatService;
    }

    /**
     * Handles incoming STOMP messages sent to {@code /app/chat}.
     * Streams the Ollama response chunk-by-chunk to the requesting user's
     * private queue {@code /user/queue/messages} so other sessions are not
     * affected. A final {@link StreamDownstreamEvent} with {@code type=done}
     * signals end-of-stream.
     * <p>
     * // EDIT FEATURE — supports {@link ChatStompPayload.Type#EDIT} using the same transport.
     */
    @MessageMapping("/chat")
    public void handleChat(@Payload ChatStompPayload payload, Principal principal) {
        if (principal == null) {
            log.warn("Rejected /app/chat because principal is null");
            return;
        }
        if (payload == null || payload.getType() == null) {
            log.warn("Rejected /app/chat — missing payload or type");
            return;
        }

        String userName = principal.getName();
        String streamId = UUID.randomUUID().toString();
        String clientStreamId = payload.getClientStreamId();
        if (clientStreamId == null || clientStreamId.isBlank()) {
            clientStreamId = UUID.randomUUID().toString();
            log.warn("Missing clientStreamId — generated server-side id={} principal={}", clientStreamId, userName);
        }

        final String outboundClientStreamId = clientStreamId;

        if (activeStreamRegistry.isDuplicateClientStream(userName, outboundClientStreamId)) {
            log.info("Ignoring duplicate /app/chat — stream already active principal={} clientStreamId={}",
                    userName, outboundClientStreamId);
            return;
        }

        String latestUser = payload.getContent();
        if (latestUser == null || latestUser.isBlank()) {
            sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, null, ERROR_PREFIX + "Message must not be empty."));
            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId));
            return;
        }

        // EDIT FEATURE — validate edit targets when regenerating a mid-thread answer
        if (payload.getType() == ChatStompPayload.Type.EDIT) {
            if (payload.getEditTargetMessageId() == null || payload.getEditTargetMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, payload.getMessageId(), ERROR_PREFIX + "EDIT requires editTargetMessageId."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, payload.getMessageId()));
                return;
            }
            if (payload.getMessageId() == null || payload.getMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, null, ERROR_PREFIX + "EDIT requires messageId (assistant bubble)."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId));
                return;
            }
        } else if (payload.getType() == ChatStompPayload.Type.NEW) {
            if (payload.getMessageId() == null || payload.getMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, null, ERROR_PREFIX + "NEW requires messageId (assistant bubble)."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId));
                return;
            }
            if (payload.getUserMessageId() == null || payload.getUserMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, null, ERROR_PREFIX + "NEW requires userMessageId (user bubble)."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId));
                return;
            }
        }

        final String assistantBubbleId = payload.getMessageId();

        final long persistedSessionId;
        try {
            ChatSession session = chatService.resolveStreamingSessionForUser(userName, payload.getSessionId(), latestUser);
            persistedSessionId = session.getId();
        } catch (RuntimeException ex) {
            log.warn("Session resolve failed for principal={}: {}", userName, ex.getMessage());
            sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId, ERROR_PREFIX + ex.getMessage()));
            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId));
            return;
        }

        String composedPrompt = ChatPromptComposer.compose(payload.getPriorMessages(), latestUser);
        log.info("WebSocket /app/chat — principal={}, type={}, assistantBubbleId={}, editTarget={}, clientStreamId={}, sessionId={}, promptChars={}",
                userName,
                payload.getType(),
                payload.getMessageId(),
                payload.getEditTargetMessageId(),
                outboundClientStreamId,
                persistedSessionId,
                composedPrompt.length());

        try {
            chatService.beginStreamingTurn(
                    userName,
                    payload.getType(),
                    persistedSessionId,
                    latestUser,
                    payload.getType() == ChatStompPayload.Type.NEW ? payload.getUserMessageId() : null,
                    assistantBubbleId,
                    payload.getType() == ChatStompPayload.Type.EDIT ? payload.getEditTargetMessageId() : null
            );
        } catch (RuntimeException ex) {
            log.warn("beginStreamingTurn failed principal={}, sessionId={}: {}", userName, persistedSessionId, ex.getMessage());
            sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId, ERROR_PREFIX + ex.getMessage()));
            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId));
            return;
        }

        StringBuilder assistantAccumulator = new StringBuilder();
        AtomicBoolean persisted = new AtomicBoolean(false);
        AtomicLong lastPartialPersistMs = new AtomicLong(0);

        Runnable persistOnce = () -> {
            if (!persisted.compareAndSet(false, true)) {
                return;
            }
            try {
                chatService.persistWebsocketTurn(
                        userName,
                        payload.getType(),
                        persistedSessionId,
                        latestUser,
                        payload.getType() == ChatStompPayload.Type.NEW ? payload.getUserMessageId() : null,
                        assistantBubbleId,
                        payload.getType() == ChatStompPayload.Type.EDIT ? payload.getEditTargetMessageId() : null,
                        assistantAccumulator.toString()
                );
            } catch (RuntimeException ex) {
                log.warn("Persist failed principal={}, sessionId={}: {}", userName, persistedSessionId, ex.getMessage());
            }
        };

        // Subscribe inside replaceAndStart so the Disposable is registered in the same
        // atomic step as cancelling any prior stream — avoids Disposable.Swap races
        // where STOP disposed before swap.update(...) and left Ollama running.
        activeStreamRegistry.replaceAndStart(
                userName,
                streamId,
                outboundClientStreamId,
                persistedSessionId,
                assistantBubbleId,
                () ->
                streamingService.streamChat(composedPrompt)
                        .doOnNext(chunk -> {
                            log.debug("Sending chunk to principal={} clientStreamId={} chunk='{}'",
                                    userName, outboundClientStreamId, chunk);
                            if (chunk != null && chunk.startsWith(ERROR_PREFIX)) {
                                assistantAccumulator.append(chunk);
                                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId, chunk), persistedSessionId);
                            } else if (chunk != null) {
                                assistantAccumulator.append(chunk);
                                sendJsonToUser(userName, StreamDownstreamEvent.chunk(outboundClientStreamId, assistantBubbleId, chunk));
                                long now = System.currentTimeMillis();
                                if (now - lastPartialPersistMs.get() >= PARTIAL_PERSIST_INTERVAL_MS) {
                                    lastPartialPersistMs.set(now);
                                    try {
                                        chatService.updatePartialAiResponse(
                                                userName,
                                                persistedSessionId,
                                                assistantBubbleId,
                                                assistantAccumulator.toString()
                                        );
                                    } catch (RuntimeException ex) {
                                        log.debug("Partial persist skipped principal={}: {}", userName, ex.getMessage());
                                    }
                                }
                            }
                        })
                        .doOnComplete(() -> {
                            persistOnce.run();
                            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId), persistedSessionId);
                            log.info("Streaming complete — sent DONE envelope principal={} clientStreamId={}",
                                    userName, outboundClientStreamId);
                        })
//                        .doOnError(e -> {
//                            log.error("Streaming error — principal={}, clientStreamId={}, error={}",
//                                    userName, outboundClientStreamId, e.getMessage(), e);
//                            assistantAccumulator.append(ERROR_PREFIX).append(e.getMessage());
//                            persistOnce.run();
//                            sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId,
//                                    ERROR_PREFIX + e.getMessage()), persistedSessionId);
//                            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId), persistedSessionId);
//                        })
                        .doOnError(e -> {
                            if (isTransientStreamingFailure(e)) {
                                log.info(
                                        "Transient disconnect detected. Keeping stream recoverable. principal={}, clientStreamId={}",
                                        userName,
                                        outboundClientStreamId
                                );
                                // NO error event, NO done event, NO persistOnce — partial DB state remains.
                                return;
                            }

                            log.error(
                                    "Streaming error — principal={}, clientStreamId={}, error={}",
                                    userName,
                                    outboundClientStreamId,
                                    e.getMessage(),
                                    e
                            );

                            assistantAccumulator.append(ERROR_PREFIX)
                                    .append(e.getMessage());

                            persistOnce.run();

                            sendJsonToUser(
                                    userName,
                                    StreamDownstreamEvent.error(
                                            outboundClientStreamId,
                                            assistantBubbleId,
                                            ERROR_PREFIX + e.getMessage()
                                    ),
                                    persistedSessionId
                            );

                            sendJsonToUser(
                                    userName,
                                    StreamDownstreamEvent.done(
                                            outboundClientStreamId,
                                            assistantBubbleId
                                    ),
                                    persistedSessionId
                            );
                        })

                        .doOnCancel(() -> log.info("Streaming cancelled by reactor — principal={}, streamId={}, clientStreamId={}",
                                userName, streamId, outboundClientStreamId))
//                        .doFinally(signalType -> {
//                            if (signalType == SignalType.CANCEL) {
//                                persistOnce.run();
//                            }
//                            boolean cleaned = activeStreamRegistry.removeIfMatches(userName, streamId);
//                            if (cleaned) {
//                                log.info("Cleanup completed — principal={}, streamId={}, clientStreamId={}, signal={}",
//                                        userName, streamId, outboundClientStreamId, signalType);
//                            } else {
//                                log.debug("Cleanup skipped (stream already replaced/removed) — principal={}, streamId={}, signal={}",
//                                        userName, streamId, signalType);
//                            }
//                        })
                        .doFinally(signalType -> {

                            boolean userStopped =
                                    signalType == SignalType.CANCEL &&
                                            !activeStreamRegistry.hasActiveStream(userName);

                            if (userStopped) {
                                persistOnce.run();
                            }

                            boolean cleaned =
                                    activeStreamRegistry.removeIfMatches(userName, streamId);

                            if (cleaned) {

                                log.info(
                                        "Cleanup completed — principal={}, streamId={}, clientStreamId={}, signal={}",
                                        userName,
                                        streamId,
                                        outboundClientStreamId,
                                        signalType
                                );

                            } else {

                                log.debug(
                                        "Cleanup skipped (stream already replaced/removed) — principal={}, streamId={}, signal={}",
                                        userName,
                                        streamId,
                                        signalType
                                );
                            }
                        })
                        .subscribe());
    }

    /**
     * // UPDATED — STOP now emits a DONE envelope tagged with the active {@code clientStreamId}.
     */
    @MessageMapping("/chat/stop")
    public void handleStop(@Payload(required = false) String ignoredBody, Principal principal) {
        if (principal == null) {
            log.warn("Ignored /app/chat/stop because principal is null");
            return;
        }
        String userName = principal.getName();
        activeStreamRegistry.cancel(userName, "Client requested STOP")
                .ifPresent(clientStreamId ->
                        sendJsonToUser(userName, StreamDownstreamEvent.done(clientStreamId)));
    }

    private void sendJsonToUser(String userName, StreamDownstreamEvent event) {
        sendJsonToUser(userName, event, null);
    }

    private void sendJsonToUser(String userName, StreamDownstreamEvent event, Long chatSessionId) {
        if (chatSessionId != null) {
            event.setChatSessionId(chatSessionId);
        }
        messagingTemplate.convertAndSendToUser(
                userName,
                USER_QUEUE,
                event
        );
    }
}
