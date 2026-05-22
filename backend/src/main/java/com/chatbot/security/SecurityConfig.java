package com.chatbot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.chatbot.security.JwtFilter;

import static com.chatbot.constant.AppConstants.AUTH_BASE_PATH;
import static com.chatbot.constant.AppConstants.AUTH_LOGIN_PATH;
import static com.chatbot.constant.AppConstants.AUTH_REGISTER_PATH;
import static com.chatbot.constant.AppConstants.CHAT_ALL;
import static com.chatbot.constant.AppConstants.H2_CONSOLE_ALL;
import static com.chatbot.constant.AppConstants.WS_CHAT_ALL;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                AUTH_BASE_PATH + AUTH_LOGIN_PATH,
                                AUTH_BASE_PATH + AUTH_REGISTER_PATH).permitAll()
                        .requestMatchers(H2_CONSOLE_ALL, WS_CHAT_ALL).permitAll()
                        .requestMatchers(CHAT_ALL).authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
