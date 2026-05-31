package com.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RedisInfraStateStore {

    private static final Logger log = LoggerFactory.getLogger(RedisInfraStateStore.class);

    private static final String ACTIVE_STREAM_PREFIX = "chatbot:stream:active:";
    private static final String DISCONNECTED_ZSET_KEY = "chatbot:stream:disconnected";
    private static final String RATE_LIMIT_PREFIX = "chatbot:ratelimit:";
    private static final int MAX_EXPIRED_LOOKUP = 256;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration activeStreamTtl;
    private final Duration disconnectedMarkerTtl;
    private final String instanceId;

    private final ConcurrentMap<String, ActiveStreamState> inMemoryActiveStreams = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> inMemoryDisconnected = new ConcurrentHashMap<>();
    private final AtomicBoolean redisWarningLogged = new AtomicBoolean(false);

    public RedisInfraStateStore(
            ObjectProvider<StringRedisTemplate> redisTemplateProvider,
            ObjectMapper objectMapper,
            @Value("${app.redis.stream.active-ttl-seconds:1800}") long activeStreamTtlSeconds,
            @Value("${app.redis.stream.disconnected-ttl-seconds:3600}") long disconnectedTtlSeconds) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
        this.objectMapper = objectMapper;
        this.activeStreamTtl = Duration.ofSeconds(Math.max(60, activeStreamTtlSeconds));
        this.disconnectedMarkerTtl = Duration.ofSeconds(Math.max(60, disconnectedTtlSeconds));
        this.instanceId = UUID.randomUUID().toString();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void saveActiveStream(String principalName, ActiveStreamState state) {
        inMemoryActiveStreams.put(principalName, state);
        withRedis(() -> redisTemplate.opsForValue().set(activeStreamKey(principalName), toJson(state), activeStreamTtl));
    }

    public Optional<ActiveStreamState> getActiveStream(String principalName) {
        Optional<ActiveStreamState> redis = fromRedis(() ->
                redisTemplate.opsForValue().get(activeStreamKey(principalName)))
                .flatMap(this::fromJson);
        if (redis.isPresent()) {
            return redis;
        }
        return Optional.ofNullable(inMemoryActiveStreams.get(principalName));
    }

    public Optional<ActiveStreamState> removeActiveStream(String principalName) {
        ActiveStreamState localRemoved = inMemoryActiveStreams.remove(principalName);
        Optional<ActiveStreamState> redisRemoved = fromRedis(() -> {
            String key = activeStreamKey(principalName);
            String payload = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);
            return payload;
        }).flatMap(this::fromJson);
        return redisRemoved.isPresent() ? redisRemoved : Optional.ofNullable(localRemoved);
    }

    public boolean removeActiveStreamIfMatches(String principalName, String streamId) {
        Optional<ActiveStreamState> current = getActiveStream(principalName);
        if (current.isEmpty() || !streamId.equals(current.get().streamId())) {
            return false;
        }
        removeActiveStream(principalName);
        return true;
    }

    public boolean hasActiveStream(String principalName) {
        if (fromRedis(() -> Boolean.TRUE.equals(redisTemplate.hasKey(activeStreamKey(principalName)))).orElse(false)) {
            return true;
        }
        return inMemoryActiveStreams.containsKey(principalName);
    }

    public boolean isActiveStreamId(String principalName, String streamId) {
        if (streamId == null || streamId.isBlank()) {
            return false;
        }
        Optional<ActiveStreamState> active = getActiveStream(principalName);
        return active.isPresent() && streamId.equals(active.get().streamId());
    }

    public void markDisconnected(String principalName, long disconnectedAtEpochMs) {
        inMemoryDisconnected.put(principalName, disconnectedAtEpochMs);
        withRedis(() -> redisTemplate.opsForZSet()
                .add(DISCONNECTED_ZSET_KEY, principalName, disconnectedAtEpochMs));
        withRedis(() -> redisTemplate.expire(DISCONNECTED_ZSET_KEY, disconnectedMarkerTtl));
    }

    public void clearDisconnected(String principalName) {
        inMemoryDisconnected.remove(principalName);
        withRedis(() -> redisTemplate.opsForZSet().remove(DISCONNECTED_ZSET_KEY, principalName));
    }

    public List<String> getExpiredDisconnectedUsers(long cutoffEpochMs) {
        Optional<Set<String>> redisUsers = fromRedis(() -> redisTemplate.opsForZSet()
                .rangeByScore(DISCONNECTED_ZSET_KEY, 0, cutoffEpochMs, 0, MAX_EXPIRED_LOOKUP));
        if (redisUsers.isPresent()) {
            return new ArrayList<>(redisUsers.get());
        }
        List<String> expired = new ArrayList<>();
        inMemoryDisconnected.forEach((principalName, disconnectedAt) -> {
            if (disconnectedAt <= cutoffEpochMs) {
                expired.add(principalName);
            }
        });
        return expired;
    }

    /**
     * Structure-only helper so route-level limits can be introduced without changing key design later.
     */
    public long incrementRateLimitCounter(String principalName, String routeKey, Duration window) {
        String safePrincipal = (principalName == null || principalName.isBlank()) ? "anonymous" : principalName;
        String safeRoute = (routeKey == null || routeKey.isBlank()) ? "default" : routeKey;
        Duration safeWindow = (window == null || window.isZero() || window.isNegative()) ? Duration.ofSeconds(60) : window;
        long bucket = Instant.now().toEpochMilli() / Math.max(1000, safeWindow.toMillis());
        String key = RATE_LIMIT_PREFIX + safeRoute + ":" + safePrincipal + ":" + bucket;

        Optional<Long> value = fromRedis(() -> redisTemplate.opsForValue().increment(key));
        if (value.isPresent()) {
            withRedis(() -> redisTemplate.expire(key, safeWindow));
            return value.get();
        }
        return 1L;
    }

    private String activeStreamKey(String principalName) {
        return ACTIVE_STREAM_PREFIX + principalName;
    }

    private String toJson(ActiveStreamState state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize active stream state", e);
        }
    }

    private Optional<ActiveStreamState> fromJson(String payload) {
        try {
            return Optional.of(objectMapper.readValue(payload, ActiveStreamState.class));
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse active stream state from Redis: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private void withRedis(Runnable runnable) {
        if (redisTemplate == null) {
            return;
        }
        try {
            runnable.run();
            redisWarningLogged.set(false);
        } catch (RuntimeException ex) {
            warnRedisUnavailable(ex);
        }
    }

    private <T> Optional<T> fromRedis(SupplierWithException<T> supplier) {
        if (redisTemplate == null) {
            return Optional.empty();
        }
        try {
            T value = supplier.get();
            redisWarningLogged.set(false);
            return Optional.ofNullable(value);
        } catch (RuntimeException ex) {
            warnRedisUnavailable(ex);
            return Optional.empty();
        }
    }

    private void warnRedisUnavailable(Exception ex) {
        if (redisWarningLogged.compareAndSet(false, true)) {
            log.warn("Redis unavailable, using in-memory fallback: {}", ex.getMessage());
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }

    public record ActiveStreamState(
            String streamId,
            String clientStreamId,
            Long sessionId,
            String assistantMessageClientId,
            String ownerInstanceId
    ) {
    }
}
