package com.chatbot.service;

public interface MailService {

    void sendPasswordResetEmail(String toEmail, String rawToken);
}
