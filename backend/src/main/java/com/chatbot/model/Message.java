package com.chatbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession chatSession;

    @Column(nullable = false, length = 10000)
    private String userMessage;

    @Column(nullable = false, length = 10000)
    private String aiResponse;

    @Column(nullable = false)
    private Instant timestamp;

    /** Client-generated id for the user bubble (WebSocket turns); used for EDIT correlation. */
    @Column(length = 128)
    private String userBubbleClientId;

    /** Client-generated id for the assistant bubble receiving the stream. */
    @Column(length = 128)
    private String assistantBubbleClientId;
}
