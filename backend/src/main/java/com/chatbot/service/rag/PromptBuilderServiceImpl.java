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
SYSTEM:
You are a helpful AI assistant.
Answer ONLY using the provided CONTEXT.
If the answer is not present in the CONTEXT, say exactly:
"I couldn't find that information in the uploaded document."
Never fabricate facts or invent missing information.
Keep answers concise.

CONTEXT:
%s

QUESTION:
%s
""".formatted(context, question);

    }

}
