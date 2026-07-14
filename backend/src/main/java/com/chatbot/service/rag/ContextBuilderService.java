package com.chatbot.service.rag;

import com.chatbot.model.DocumentChunk;

import java.util.List;

public interface ContextBuilderService {

    String buildContext(List<DocumentChunk> chunks);

}
