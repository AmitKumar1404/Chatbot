package com.chatbot.service;

import com.chatbot.dto.AuthResponse;
import com.chatbot.dto.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    AuthResponse register(RegisterRequest request);
}
