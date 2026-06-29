package com.chatbot.constant;

public final class AppConstants {

    private AppConstants() {
    }

    public static final String AUTH_BASE_PATH = "/auth";
    public static final String AUTH_REGISTER_PATH = "/register";
    public static final String AUTH_LOGIN_PATH = "/login";
    public static final String AUTH_REFRESH_PATH = "/refresh";
    public static final String AUTH_FORGOT_PASSWORD_PATH = "/forgot-password";
    public static final String AUTH_RESET_PASSWORD_PATH = "/reset-password";
    public static final String CHAT_BASE_PATH = "/chat";
    public static final String SEARCH_BASE_PATH = "/search";
    public static final String H2_CONSOLE_PATH = "/h2-console";

    public static final String AUTH_ALL = "/auth/**";
    public static final String CHAT_ALL = "/chat/**";
    public static final String SEARCH_ALL = "/search/**";
    public static final String H2_CONSOLE_ALL = "/h2-console/**";
    public static final String WS_CHAT_ALL = "/ws-chat/**";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_USER = "USER";

    public static final String AI_SERVICE_ERROR = "AI service error. Please try again.";
    public static final String SESSION_NOT_FOUND = "Session not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String NOT_AUTHENTICATED = "Not authenticated";
    public static final String USERNAME_ALREADY_TAKEN = "Username already taken";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String INVALID_OR_EXPIRED_REFRESH_TOKEN = "Invalid or expired refresh token";
    public static final String INVALID_OR_EXPIRED_RESET_TOKEN = "Invalid or expired reset token";
    public static final String EMAIL_ALREADY_TAKEN = "Email already taken";
    public static final String PASSWORD_RESET_EMAIL_SENT =
            "If an account with that username exists, a password reset link has been sent to the registered email.";
    public static final String PASSWORD_RESET_SUCCESS = "Password reset successful. You can now sign in.";
    public static final String NEW_CHAT_TITLE = "New chat";

    public static final String AUTH_RESPONSE_TOKEN_FIELD = "token";
    public static final String AUTH_RESPONSE_USERNAME_FIELD = "username";
    public static final String AUTH_RESPONSE_REFRESH_TOKEN_FIELD = "refreshToken";
}
