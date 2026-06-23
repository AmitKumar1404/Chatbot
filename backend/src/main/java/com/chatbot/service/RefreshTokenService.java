package com.chatbot.service;

import com.chatbot.dto.AuthResponse;

public interface RefreshTokenService {

    String createRefreshToken(String username);

    AuthResponse refreshAccessToken(String rawRefreshToken);
}
