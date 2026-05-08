package com.chatbot.service;

import com.chatbot.model.Message;

import java.util.List;

public interface AIService {

    String chat(String userMessage);

    /**
     * Multi-turn overload. Implementations that support conversation history should override this.
     * The default delegates to the single-turn method so existing clients remain unaffected.
     *
     * @param userMessage current user input
     * @param history     previous messages for this session, ordered oldest-first
     */
    default String chat(String userMessage, List<Message> history) {
        return chat(userMessage);
    }
}
