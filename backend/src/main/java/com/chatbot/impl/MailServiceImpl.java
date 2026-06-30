package com.chatbot.impl;

import com.chatbot.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String resetPasswordUrl;

    public MailServiceImpl(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress,
            @Value("${app.frontend.reset-password-url}") String resetPasswordUrl) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String rawToken) {
        String resetLink = resetPasswordUrl + "?token=" + rawToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Reset your Chatbot password");
        message.setText(
                "You requested a password reset for your Chatbot account.\n\n"
                        + "Click the link below to reset your password (valid for 1 hour):\n"
                        + resetLink
                        + "\n\nIf you did not request this, you can ignore this email.");
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}", toEmail, ex);
        }
    }
}
