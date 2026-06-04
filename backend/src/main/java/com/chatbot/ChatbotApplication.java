package com.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.chatbot.repository")
@EntityScan(basePackages = "com.chatbot.model")
@EnableScheduling
public class ChatbotApplication {

    public static void main(String[] args) {
        // Prefer IPv4 for HTTPS outbound (avoids "No route to host" to IPv6 when v6 is broken/unrouted, common on some networks/macOS)
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(ChatbotApplication.class, args);
    }
}
