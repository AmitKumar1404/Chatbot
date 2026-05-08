package com.chatbot.controller;

import com.chatbot.service.ActiveStreamRegistry;
import com.chatbot.service.OllamaStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import static com.chatbot.constant.StreamConstants.DONE;
import static com.chatbot.constant.StreamConstants.ERROR_PREFIX;
import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);
    private static final String USER_QUEUE = "/queue/messages";
//    private static final String SENTINEL_DONE = "[DONE]";

    private final OllamaStreamingService streamingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ActiveStreamRegistry activeStreamRegistry;

    public ChatWebSocketController(OllamaStreamingService streamingService,
                                   SimpMessagingTemplate messagingTemplate,
                                   ActiveStreamRegistry activeStreamRegistry) {
        this.streamingService = streamingService;
        this.messagingTemplate = messagingTemplate;
        this.activeStreamRegistry = activeStreamRegistry;
    }

    /**
     * Handles incoming STOMP messages sent to {@code /app/chat}.
     * Streams the Ollama response chunk-by-chunk to the requesting user's
     * private queue {@code /user/queue/messages} so other sessions are not
     * affected. A final {@code "[DONE]"} sentinel signals end-of-stream.
     *
     * The {@link Principal} is assigned at WebSocket handshake time (UUID-based)
     * so {@code convertAndSendToUser} can resolve the correct STOMP session via
     * the {@code SimpUserRegistry} — unlike passing a raw session ID which is
     * never registered as a principal name.
     *
     * @param userMessage the raw user prompt received from the client
     * @param principal   the per-connection principal assigned at handshake
     */
    @MessageMapping("/chat")
    public void handleChat(@Payload String userMessage, Principal principal) {
        if (principal == null) {
            log.warn("Rejected /app/chat because principal is null");
            return;
        }
        String userName = principal.getName();
        String streamId = UUID.randomUUID().toString();
        log.info("WebSocket /app/chat — principal={}, messageLength={}",
                userName, userMessage == null ? 0 : userMessage.length());

        // Subscribe inside replaceAndStart so the Disposable is registered in the same
        // atomic step as cancelling any prior stream — avoids Disposable.Swap races
        // where STOP disposed before swap.update(...) and left Ollama running.
        activeStreamRegistry.replaceAndStart(userName, streamId, () ->
                streamingService.streamChat(userMessage)
                        .doOnNext(chunk -> {
                            log.debug("Sending chunk to principal={} chunk='{}'", userName, chunk);
                            messagingTemplate.convertAndSendToUser(userName, USER_QUEUE, chunk);
                        })
                        .doOnComplete(() -> {
                            messagingTemplate.convertAndSendToUser(userName, USER_QUEUE, DONE);
                            log.info("Streaming complete — sent '{}' to principal={}", DONE, userName);
                        })
                        .doOnError(e -> {
                            log.error("Streaming error — principal={}, error={}", userName, e.getMessage(), e);
                            messagingTemplate.convertAndSendToUser(userName, USER_QUEUE, ERROR_PREFIX + e.getMessage());
                        })
                        .doOnCancel(() -> log.info("Streaming cancelled by reactor — principal={}, streamId={}", userName, streamId))
                        .doFinally(signalType -> {
                            boolean cleaned = activeStreamRegistry.removeIfMatches(userName, streamId);
                            if (cleaned) {
                                log.info("Cleanup completed — principal={}, streamId={}, signal={}", userName, streamId, signalType);
                            } else {
                                log.debug("Cleanup skipped (stream already replaced/removed) — principal={}, streamId={}, signal={}",
                                        userName, streamId, signalType);
                            }
                        })
                        .subscribe());
    }

    @MessageMapping("/chat/stop")
    public void handleStop(Principal principal) {
        if (principal == null) {
            log.warn("Ignored /app/chat/stop because principal is null");
            return;
        }
        String userName = principal.getName();
        boolean cancelled = activeStreamRegistry.cancel(userName, "Client requested STOP");
        if (cancelled) {
            // Notify frontend so stream state converges even if STOP arrives before local UI update.
            messagingTemplate.convertAndSendToUser(userName, USER_QUEUE, DONE);
        }
    }
}
