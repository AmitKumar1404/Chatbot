package com.chatbot.service.rag;

public interface PromptBuilderService {

    String buildPrompt(
            String context,
            String question
    );

}