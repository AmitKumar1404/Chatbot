package com.chatbot.service;

import reactor.core.publisher.Flux;

public interface OllamaStreamingService {

    /**
     * Streams a response from Ollama for the given user message.
     * Each emitted item is a partial text chunk extracted from the streaming JSON response.
     * The terminal item is the sentinel value {@code "[DONE]"}.
     *
     * @param userMessage the prompt to send to the model
     * @return a non-blocking {@link Flux} of text chunks
     */
    Flux<String> streamChat(String userMessage);
}
