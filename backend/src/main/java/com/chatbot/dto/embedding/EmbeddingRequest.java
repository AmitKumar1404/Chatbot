package com.chatbot.dto.embedding;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbeddingRequest {

    private String model;

    private String prompt;

}