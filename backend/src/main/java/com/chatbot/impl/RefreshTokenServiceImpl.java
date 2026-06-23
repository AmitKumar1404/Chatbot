package com.chatbot.impl;

import com.chatbot.dto.AuthResponse;
import com.chatbot.model.RefreshToken;
import com.chatbot.model.User;
import com.chatbot.repository.RefreshTokenRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.security.JwtUtil;
import com.chatbot.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

import static com.chatbot.constant.AppConstants.INVALID_OR_EXPIRED_REFRESH_TOKEN;
import static com.chatbot.constant.AppConstants.USER_NOT_FOUND;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final long refreshExpirationMs;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtUtil jwtUtil,
            @Value("${app.jwt.refresh-expiration}") long refreshExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public String createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        return persistRefreshToken(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new BadCredentialsException(INVALID_OR_EXPIRED_REFRESH_TOKEN));

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
            throw new BadCredentialsException(INVALID_OR_EXPIRED_REFRESH_TOKEN);
        }

        User user = existing.getUser();
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        String newRefreshToken = persistRefreshToken(user);

        return AuthResponse.builder()
                .token(newAccessToken)
                .username(user.getUsername())
                .refreshToken(newRefreshToken)
                .build();
    }

    private String persistRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
