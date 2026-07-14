package com.chatbot.service.rag;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilderServiceImpl
        implements PromptBuilderService {

    @Override
    public String buildPrompt(
            String context,
            String question) {

        return """
You are a helpful AI assistant.

Answer ONLY using the provided context.

If the answer is not present in the context,
say:

"I couldn't find that information in the uploaded document."

Context:

%s

Question:

%s
""".formatted(context, question);

    }

}