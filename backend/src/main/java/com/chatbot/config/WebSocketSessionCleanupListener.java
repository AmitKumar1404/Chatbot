package com.chatbot.config;

import com.chatbot.service.ActiveStreamRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketSessionCleanupListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionCleanupListener.class);

    private final ActiveStreamRegistry activeStreamRegistry;

    public WebSocketSessionCleanupListener(ActiveStreamRegistry activeStreamRegistry) {
        this.activeStreamRegistry = activeStreamRegistry;
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal == null) {
            return;
        }

        boolean cancelled = activeStreamRegistry.cancel(principal.getName(), "WebSocket session disconnected");
        if (cancelled) {
            log.info("Cleanup completed after disconnect — principal={}, sessionId={}",
                    principal.getName(), accessor.getSessionId());
        }
    }
}
