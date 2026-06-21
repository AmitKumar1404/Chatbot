package com.chatbot.security;

import com.chatbot.ratelimit.IpRateLimitingFilter;
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
import static com.chatbot.constant.AppConstants.AUTH_REFRESH_PATH;
import static com.chatbot.constant.AppConstants.AUTH_REGISTER_PATH;
import static com.chatbot.constant.AppConstants.CHAT_ALL;
import static com.chatbot.constant.AppConstants.H2_CONSOLE_ALL;
import static com.chatbot.constant.AppConstants.SEARCH_ALL;
import static com.chatbot.constant.AppConstants.WS_CHAT_ALL;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final IpRateLimitingFilter ipRateLimitingFilter;
    private final JwtFilter jwtFilter;

    public SecurityConfig(IpRateLimitingFilter ipRateLimitingFilter, JwtFilter jwtFilter) {
        this.ipRateLimitingFilter = ipRateLimitingFilter;
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
                                AUTH_BASE_PATH + AUTH_REGISTER_PATH,
                                AUTH_BASE_PATH + AUTH_REFRESH_PATH).permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**").permitAll()
                        .requestMatchers(H2_CONSOLE_ALL, WS_CHAT_ALL).permitAll()
                        .requestMatchers(CHAT_ALL, SEARCH_ALL).authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ipRateLimitingFilter, JwtFilter.class);

        return http.build();
    }
}
