package com.chatbot.repository;

import com.chatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatSession_IdOrderByTimestampAsc(Long sessionId);

    Optional<Message> findByChatSession_IdAndUserBubbleClientId(Long sessionId, String userBubbleClientId);

    Optional<Message> findByIdAndChatSession_Id(Long id, Long sessionId);

    void deleteByChatSession_IdAndIdGreaterThan(Long sessionId, Long messageId);

    void deleteByChatSession_Id(Long sessionId);
}
