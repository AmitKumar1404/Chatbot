package com.chatbot.dto.embedding;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbeddingResponse {

    private List<Float> embedding;

}