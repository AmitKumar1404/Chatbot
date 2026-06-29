package com.chatbot.impl;

import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.ResetPasswordRequest;
import com.chatbot.model.PasswordResetToken;
import com.chatbot.model.User;
import com.chatbot.repository.PasswordResetTokenRepository;
import com.chatbot.repository.RefreshTokenRevocationRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.MailService;
import com.chatbot.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

import static com.chatbot.constant.AppConstants.INVALID_OR_EXPIRED_RESET_TOKEN;
import static com.chatbot.constant.AppConstants.PASSWORD_RESET_EMAIL_SENT;
import static com.chatbot.constant.AppConstants.PASSWORD_RESET_SUCCESS;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRevocationRepository refreshTokenRevocationRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final long resetExpirationMs;

    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            RefreshTokenRevocationRepository refreshTokenRevocationRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            @Value("${app.password-reset.expiration}") long resetExpirationMs) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenRevocationRepository = refreshTokenRevocationRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.resetExpirationMs = resetExpirationMs;
    }

    @Override
    @Transactional
    public MessageResponse requestReset(String username) {
        userRepository.findByUsername(username.trim()).ifPresent(user -> {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                passwordResetTokenRepository.revokeAllActiveByUserId(user.getId());
                String rawToken = persistResetToken(user);
                mailService.sendPasswordResetEmail(user.getEmail(), rawToken);
            }
        });
        return MessageResponse.builder()
                .message(PASSWORD_RESET_EMAIL_SENT)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        String tokenHash = hashToken(request.getToken());
        PasswordResetToken existing = passwordResetTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new RuntimeException(INVALID_OR_EXPIRED_RESET_TOKEN));

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            existing.setRevoked(true);
            passwordResetTokenRepository.save(existing);
            throw new RuntimeException(INVALID_OR_EXPIRED_RESET_TOKEN);
        }

        User user = existing.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        existing.setRevoked(true);
        passwordResetTokenRepository.save(existing);

        refreshTokenRevocationRepository.revokeAllActiveByUserId(user.getId());

        return MessageResponse.builder()
                .message(PASSWORD_RESET_SUCCESS)
                .build();
    }

    private String persistResetToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(resetExpirationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        passwordResetTokenRepository.save(resetToken);
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
