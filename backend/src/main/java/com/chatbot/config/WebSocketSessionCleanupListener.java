package com.chatbot.config;

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

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal == null) {
            return;
        }

        // Transient disconnect must not cancel in-flight generation — client recovers via REST + WS resume.
        log.info("WebSocket session disconnected — generation continues — principal={}, stompSessionId={}",
                principal.getName(), accessor.getSessionId());
    }
}
