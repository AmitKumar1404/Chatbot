package com.chatbot.service;

import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.ResetPasswordRequest;

public interface PasswordResetService {

    MessageResponse requestReset(String username);

    MessageResponse resetPassword(ResetPasswordRequest request);
}
