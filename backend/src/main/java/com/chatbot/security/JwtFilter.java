package com.chatbot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.chatbot.constant.AppConstants.AUTH_BASE_PATH;
import static com.chatbot.constant.AppConstants.AUTH_LOGIN_PATH;
import static com.chatbot.constant.AppConstants.AUTH_REFRESH_PATH;
import static com.chatbot.constant.AppConstants.AUTH_REGISTER_PATH;
import static com.chatbot.constant.AppConstants.AUTHORIZATION_HEADER;
import static com.chatbot.constant.AppConstants.BEARER_PREFIX;
import static com.chatbot.constant.AppConstants.H2_CONSOLE_PATH;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isJwtOptionalHttpPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Paths where the filter must not require a JWT (login/register, tooling).
     * Note: {@code /auth/me} is <em>not</em> listed here — it relies on this filter to populate the security context.
     */
    private static boolean isJwtOptionalHttpPath(String path) {
        return path.endsWith(AUTH_BASE_PATH + AUTH_LOGIN_PATH)
                || path.endsWith(AUTH_BASE_PATH + AUTH_REGISTER_PATH)
                || path.endsWith(AUTH_BASE_PATH + AUTH_REFRESH_PATH)
                || path.startsWith(H2_CONSOLE_PATH);
    }
}
