package com.chatbot.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Documents the explicit chatMode contract used by ChatWebSocketController.
 */
class ChatStompPayloadChatModeTest {

    @Test
    void missingChatMode_isTreatedAsNormalEvenWhenDocumentIdPresent() {
        ChatStompPayload payload = new ChatStompPayload();
        payload.setDocumentId(42L);
        assertNull(payload.getChatMode());
        assertFalse(payload.getChatMode() == ChatStompPayload.ChatMode.DOCUMENT);
    }

    @Test
    void documentMode_requiresExplicitEnum() {
        ChatStompPayload payload = new ChatStompPayload();
        payload.setChatMode(ChatStompPayload.ChatMode.DOCUMENT);
        payload.setDocumentId(42L);
        assertEquals(ChatStompPayload.ChatMode.DOCUMENT, payload.getChatMode());
        assertEquals(42L, payload.getDocumentId());
    }

    @Test
    void normalMode_ignoresDocumentIdAtContractLevel() {
        ChatStompPayload payload = new ChatStompPayload();
        payload.setChatMode(ChatStompPayload.ChatMode.NORMAL);
        payload.setDocumentId(99L);
        assertEquals(ChatStompPayload.ChatMode.NORMAL, payload.getChatMode());
        assertTrue(payload.getChatMode() != ChatStompPayload.ChatMode.DOCUMENT);
    }
}
