package com.chatbot.service.rag;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderServiceImplTest {

    private final PromptBuilderService promptBuilderService = new PromptBuilderServiceImpl();

    @Test
    void relatedQuestionWithAnswerAbsent_keepsExactUnavailablePhrase() {
        String prompt = promptBuilderService.buildPrompt(
                "Chunk 1:\nEducation: MCA and BCA only.\n\n",
                "What is my passport number?"
        );

        assertTrue(prompt.contains("SYSTEM:"));
        assertTrue(prompt.contains("CONTEXT:"));
        assertTrue(prompt.contains("QUESTION:"));
        assertTrue(prompt.contains("Answer ONLY using the provided CONTEXT."));
        assertTrue(prompt.contains(
                "\"I couldn't find that information in the uploaded document.\""
        ));
        assertTrue(prompt.contains("What is my passport number?"));
    }
}
