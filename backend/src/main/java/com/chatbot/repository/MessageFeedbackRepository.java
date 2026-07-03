package com.chatbot.repository;

import com.chatbot.model.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {

    Optional<MessageFeedback> findByMessage_IdAndUser_Id(Long messageId, Long userId);

    boolean existsByMessage_IdAndUser_Id(Long messageId, Long userId);
}
