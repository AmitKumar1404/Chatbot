package com.chatbot.service;

import reactor.core.publisher.Flux;

public interface OllamaStreamingService {

    Flux<String> streamChat(String userMessage);
}
