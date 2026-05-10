package com.chatbot.controller;

import com.chatbot.dto.ChatStompPayload;
import com.chatbot.dto.StreamDownstreamEvent;
import com.chatbot.service.ActiveStreamRegistry;
import com.chatbot.service.ChatPromptComposer;
import com.chatbot.service.OllamaStreamingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

import static com.chatbot.constant.StreamConstants.ERROR_PREFIX;

@Controller
public class ChatWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);
    private static final String USER_QUEUE = "/queue/messages";

    private final OllamaStreamingService streamingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveStreamRegistry activeStreamRegistry;
    private final ObjectMapper objectMapper;

    public ChatWebSocketController(OllamaStreamingService streamingService,
                                   SimpMessagingTemplate messagingTemplate,
                                   ActiveStreamRegistry activeStreamRegistry,
                                   ObjectMapper objectMapper) {
        this.streamingService = streamingService;
        this.messagingTemplate = messagingTemplate;
        this.activeStreamRegistry = activeStreamRegistry;
        this.objectMapper = objectMapper;
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

        String latestUser = payload.getContent();
        if (latestUser == null || latestUser.isBlank()) {
            sendJsonToUser(userName, StreamDownstreamEvent.error(clientStreamId, null, ERROR_PREFIX + "Message must not be empty."));
            sendJsonToUser(userName, StreamDownstreamEvent.done(clientStreamId));
            return;
        }

        // EDIT FEATURE — validate edit targets when regenerating a mid-thread answer
        if (payload.getType() == ChatStompPayload.Type.EDIT) {
            if (payload.getEditTargetMessageId() == null || payload.getEditTargetMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(clientStreamId, payload.getMessageId(), ERROR_PREFIX + "EDIT requires editTargetMessageId."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(clientStreamId, payload.getMessageId()));
                return;
            }
            if (payload.getMessageId() == null || payload.getMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(clientStreamId, null, ERROR_PREFIX + "EDIT requires messageId (assistant bubble)."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(clientStreamId));
                return;
            }
        } else if (payload.getType() == ChatStompPayload.Type.NEW) {
            if (payload.getMessageId() == null || payload.getMessageId().isBlank()) {
                sendJsonToUser(userName, StreamDownstreamEvent.error(clientStreamId, null, ERROR_PREFIX + "NEW requires messageId (assistant bubble)."));
                sendJsonToUser(userName, StreamDownstreamEvent.done(clientStreamId));
                return;
            }
        }

        final String assistantBubbleId = payload.getMessageId();

        String composedPrompt = ChatPromptComposer.compose(payload.getPriorMessages(), latestUser);
        log.info("WebSocket /app/chat — principal={}, type={}, assistantBubbleId={}, editTarget={}, clientStreamId={}, promptChars={}",
                userName,
                payload.getType(),
                payload.getMessageId(),
                payload.getEditTargetMessageId(),
                clientStreamId,
                composedPrompt.length());

        final String outboundClientStreamId = clientStreamId;

        // Subscribe inside replaceAndStart so the Disposable is registered in the same
        // atomic step as cancelling any prior stream — avoids Disposable.Swap races
        // where STOP disposed before swap.update(...) and left Ollama running.
        activeStreamRegistry.replaceAndStart(userName, streamId, outboundClientStreamId, () ->
                streamingService.streamChat(composedPrompt)
                        .doOnNext(chunk -> {
                            log.debug("Sending chunk to principal={} clientStreamId={} chunk='{}'",
                                    userName, outboundClientStreamId, chunk);
                            if (chunk != null && chunk.startsWith(ERROR_PREFIX)) {
                                sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId, chunk));
                            } else if (chunk != null) {
                                sendJsonToUser(userName, StreamDownstreamEvent.chunk(outboundClientStreamId, assistantBubbleId, chunk));
                            }
                        })
                        .doOnComplete(() -> {
                            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId));
                            log.info("Streaming complete — sent DONE envelope principal={} clientStreamId={}",
                                    userName, outboundClientStreamId);
                        })
                        .doOnError(e -> {
                            log.error("Streaming error — principal={}, clientStreamId={}, error={}",
                                    userName, outboundClientStreamId, e.getMessage(), e);
                            sendJsonToUser(userName, StreamDownstreamEvent.error(outboundClientStreamId, assistantBubbleId,
                                    ERROR_PREFIX + e.getMessage()));
                            sendJsonToUser(userName, StreamDownstreamEvent.done(outboundClientStreamId, assistantBubbleId));
                        })
                        .doOnCancel(() -> log.info("Streaming cancelled by reactor — principal={}, streamId={}, clientStreamId={}",
                                userName, streamId, outboundClientStreamId))
                        .doFinally(signalType -> {
                            boolean cleaned = activeStreamRegistry.removeIfMatches(userName, streamId);
                            if (cleaned) {
                                log.info("Cleanup completed — principal={}, streamId={}, clientStreamId={}, signal={}",
                                        userName, streamId, outboundClientStreamId, signalType);
                            } else {
                                log.debug("Cleanup skipped (stream already replaced/removed) — principal={}, streamId={}, signal={}",
                                        userName, streamId, signalType);
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
        try {
            messagingTemplate.convertAndSendToUser(userName, USER_QUEUE, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stream event for user {}", userName, e);
        }
    }
}
