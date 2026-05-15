package com.chatbot.config;

import com.chatbot.constant.AppConstants;
import com.chatbot.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Authenticates STOMP {@link StompCommand#CONNECT} using the same Bearer JWT as REST.
 * Subsequent frames reuse the user set on the session.
 */
@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(StompJwtChannelInterceptor.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public StompJwtChannelInterceptor(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = extractBearer(accessor);
            if (token == null || token.isBlank()) {
                log.warn("STOMP CONNECT rejected — missing Authorization");
                throw new MessageDeliveryException(message, "Missing or invalid Authorization");
            }
            if (!jwtUtil.validateToken(token)) {
                log.warn("STOMP CONNECT rejected — invalid JWT");
                throw new MessageDeliveryException(message, "Invalid or expired token");
            }
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Three-arg UsernamePasswordAuthenticationToken is already authenticated in Spring Security 6;
            // calling setAuthenticated(true) throws IllegalArgumentException and closes the socket (STOMP ERROR / 1002).
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
            return message;
        }

        if (requiresUser(accessor) && accessor.getUser() == null) {
            log.warn("STOMP {} rejected — unauthenticated user", accessor.getCommand());
            throw new MessageDeliveryException(message, "Unauthenticated");
        }

        return message;
    }

    private static boolean requiresUser(StompHeaderAccessor accessor) {
        if (accessor.getCommand() != StompCommand.SEND && accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return false;
        }
        String destination = accessor.getDestination();
        if (destination == null) {
            return false;
        }
        return destination.startsWith("/app/");
    }

    private static String extractBearer(StompHeaderAccessor accessor) {
        List<String> headers = accessor.getNativeHeader(AppConstants.AUTHORIZATION_HEADER);
        if (headers == null || headers.isEmpty()) {
            headers = accessor.getNativeHeader("authorization");
        }
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        String raw = headers.get(0);
        if (raw == null || !raw.startsWith(AppConstants.BEARER_PREFIX)) {
            return null;
        }
        return raw.substring(AppConstants.BEARER_PREFIX.length()).trim();
    }
}