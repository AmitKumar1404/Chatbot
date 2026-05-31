package com.chatbot.config;

import com.chatbot.service.ActiveStreamRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketSessionCleanupListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionCleanupListener.class);
    private static final long DISCONNECT_GRACE_PERIOD_MS = 120_000;

    private final ActiveStreamRegistry activeStreamRegistry;
    private final SimpUserRegistry simpUserRegistry;

    public WebSocketSessionCleanupListener(ActiveStreamRegistry activeStreamRegistry, SimpUserRegistry simpUserRegistry) {
        this.activeStreamRegistry = activeStreamRegistry;
        this.simpUserRegistry = simpUserRegistry;
    }

    @EventListener
    public void onSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal == null) {
            return;
        }
        activeStreamRegistry.clearDisconnected(principal.getName());
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal == null) {
            return;
        }

        String principalName = principal.getName();
        activeStreamRegistry.markDisconnected(principalName, System.currentTimeMillis());

        // Transient disconnect gets a grace window for reconnect/recovery.
        log.info("WebSocket session disconnected — waiting for reconnect grace period — principal={}, stompSessionId={}",
                principal.getName(), accessor.getSessionId());
    }

    @Scheduled(fixedDelay = 30_000)
    public void cleanupStaleDisconnectedStreams() {
        long now = System.currentTimeMillis();
        long cutoff = now - DISCONNECT_GRACE_PERIOD_MS;
        activeStreamRegistry.expiredDisconnectedUsers(cutoff).forEach(principalName -> {
            if (simpUserRegistry.getUser(principalName) != null) {
                activeStreamRegistry.clearDisconnected(principalName);
                return;
            }
            activeStreamRegistry.cancel(principalName, "Disconnected beyond grace period");
            activeStreamRegistry.clearDisconnected(principalName);
        });
    }
}
