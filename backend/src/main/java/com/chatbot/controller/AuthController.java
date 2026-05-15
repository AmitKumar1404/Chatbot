package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.AuthResponse;
import com.chatbot.dto.LoginRequest;
import com.chatbot.dto.RegisterRequest;
import com.chatbot.security.JwtUtil;
import com.chatbot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chatbot.constant.AppConstants.AUTH_BASE_PATH;
import static com.chatbot.constant.AppConstants.AUTH_LOGIN_PATH;
import static com.chatbot.constant.AppConstants.AUTH_REGISTER_PATH;

@RestController
@RequestMapping(AUTH_BASE_PATH)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping(AUTH_REGISTER_PATH)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(ResponseCode.CREATED).body(response);
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtil.generateToken(request.getUsername());
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .build();
        return ResponseEntity.status(ResponseCode.OK).body(response);
    }

    /**
     * Validates the JWT (via {@code JwtFilter}) and returns the current username for client bootstrap / refresh.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(ResponseCode.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(AuthResponse.builder()
                .username(authentication.getName())
                .build());
    }
}
