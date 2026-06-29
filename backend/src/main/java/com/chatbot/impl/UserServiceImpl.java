package com.chatbot.impl;

import com.chatbot.dto.AuthResponse;
import com.chatbot.dto.RegisterRequest;
import com.chatbot.model.User;
import com.chatbot.repository.UserRepository;
import com.chatbot.security.JwtUtil;
import com.chatbot.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chatbot.constant.AppConstants.EMAIL_ALREADY_TAKEN;
import static com.chatbot.constant.AppConstants.ROLE_USER;
import static com.chatbot.constant.AppConstants.USERNAME_ALREADY_TAKEN;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(ROLE_USER)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException(USERNAME_ALREADY_TAKEN);
        }
        User.UserBuilder userBuilder = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()));
        String email = request.getEmail();
        if (email != null && !email.isBlank()) {
            userBuilder.email(email.trim());
        }
        User user = userBuilder.build();
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException(EMAIL_ALREADY_TAKEN);
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}
