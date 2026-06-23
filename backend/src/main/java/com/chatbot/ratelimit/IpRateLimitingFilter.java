package com.chatbot.ratelimit;

import com.chatbot.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chatbot.constant.AppConstants.AUTH_BASE_PATH;
import static com.chatbot.constant.AppConstants.AUTH_LOGIN_PATH;
import static com.chatbot.constant.AppConstants.CHAT_BASE_PATH;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private static final int TOO_MANY_REQUESTS = 429;
    private static final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Please try again later.";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> chatBuckets = new ConcurrentHashMap<>();

    public IpRateLimitingFilter(RateLimitProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Bucket bucket = resolveBucket(request);
        if (bucket == null || bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        ErrorResponse error = ErrorResponse.builder()
                .message(RATE_LIMIT_EXCEEDED)
                .status(TOO_MANY_REQUESTS)
                .timestamp(Instant.now())
                .build();

        response.setStatus(TOO_MANY_REQUESTS);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }

        String method = request.getMethod();
        String clientIp = resolveClientIp(request);

        if (isLoginRequest(path, method)) {
            RateLimitProperties.Rule rule = properties.getLogin();
            return loginBuckets.computeIfAbsent(clientIp, ip -> newBucket(rule));
        }

        if (isChatRequest(path, method)) {
            RateLimitProperties.Rule rule = properties.getChat();
            return chatBuckets.computeIfAbsent(clientIp, ip -> newBucket(rule));
        }

        return null;
    }

    private static boolean isLoginRequest(String path, String method) {
        return HttpMethod.POST.matches(method) && path.endsWith(AUTH_BASE_PATH + AUTH_LOGIN_PATH);
    }

    private static boolean isChatRequest(String path, String method) {
        return !HttpMethod.OPTIONS.matches(method) && path.startsWith(CHAT_BASE_PATH);
    }

    private static Bucket newBucket(RateLimitProperties.Rule rule) {
        Bandwidth limit = Bandwidth.classic(
                rule.getCapacity(),
                Refill.greedy(rule.getRefillTokens(), Duration.ofSeconds(rule.getRefillDurationSeconds()))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] parts = forwardedFor.split(",");
            if (parts.length > 0) {
                String candidate = parts[0].trim();
                if (!candidate.isBlank()) {
                    return candidate;
                }
            }
        }
        return request.getRemoteAddr();
    }
}
