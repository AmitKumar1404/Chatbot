package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.chatbot.constant.AppConstants.AUTH_RESPONSE_REFRESH_TOKEN_FIELD;
import static com.chatbot.constant.AppConstants.AUTH_RESPONSE_TOKEN_FIELD;
import static com.chatbot.constant.AppConstants.AUTH_RESPONSE_USERNAME_FIELD;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    @JsonProperty(AUTH_RESPONSE_TOKEN_FIELD)
    private String token;

    @JsonProperty(AUTH_RESPONSE_USERNAME_FIELD)
    private String username;

    @JsonProperty(AUTH_RESPONSE_REFRESH_TOKEN_FIELD)
    private String refreshToken;
}
