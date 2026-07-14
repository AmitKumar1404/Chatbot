package com.chatbot.service.rag;

import com.chatbot.model.DocumentChunk;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextBuilderServiceImpl
        implements ContextBuilderService {

    @Override
    public String buildContext(
            List<DocumentChunk> chunks) {

        StringBuilder context =
                new StringBuilder();

        for (int i = 0; i < chunks.size(); i++) {

            context.append("Chunk ")
                    .append(i + 1)
                    .append(":\n");

            context.append(chunks.get(i).getContent());

            context.append("\n\n");
        }

        return context.toString();
    }
}