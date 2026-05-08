package com.chatbot.service;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.model.ChatSession;
import com.chatbot.model.Message;

import java.util.List;

public interface ChatService {

    ChatResponse chat(ChatRequest request);

    List<ChatSession> listSessions();

    List<Message> getMessages(Long sessionId);
}
