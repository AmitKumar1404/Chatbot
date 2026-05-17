package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.dto.UpdateSessionTitleRequest;
import com.chatbot.model.ChatSession;
import com.chatbot.model.Message;
import com.chatbot.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.chatbot.constant.AppConstants.CHAT_BASE_PATH;

@RestController
@RequestMapping(CHAT_BASE_PATH)
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.status(ResponseCode.OK).body(chatService.chat(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> sessions() {
        return ResponseEntity.status(ResponseCode.OK).body(chatService.listSessions());
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createSession() {
        return ResponseEntity.status(ResponseCode.CREATED).body(chatService.createEmptySession());
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return ResponseEntity.status(ResponseCode.OK).build();
    }

    @PatchMapping("/sessions/{sessionId}/title")
    public ResponseEntity<ChatSession> updateSessionTitle(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionTitleRequest request) {
        return ResponseEntity.status(ResponseCode.OK).body(chatService.updateSessionTitle(sessionId, request.getTitle()));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<Message>> messages(@PathVariable Long sessionId) {
        return ResponseEntity.status(ResponseCode.OK).body(chatService.getMessages(sessionId));
    }
}
