package com.chatbot.repository;

import com.chatbot.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatSession_IdOrderByTimestampAsc(Long sessionId);

    @Query("""
            select m
            from Message m
            where m.chatSession.id = :sessionId
            order by m.timestamp asc
            """)
    Page<Message> findPageByChatSessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    Optional<Message> findByChatSession_IdAndUserBubbleClientId(Long sessionId, String userBubbleClientId);

    Optional<Message> findByChatSession_IdAndAssistantBubbleClientId(Long sessionId, String assistantBubbleClientId);

    Optional<Message> findByIdAndChatSession_Id(Long id, Long sessionId);

    void deleteByChatSession_IdAndIdGreaterThan(Long sessionId, Long messageId);

    void deleteByChatSession_Id(Long sessionId);

    @Query("""
            select m
            from Message m
            where m.chatSession.user.id = :userId
              and (
                   lower(m.userMessage) like lower(concat('%', :keyword, '%'))
                or lower(m.aiResponse) like lower(concat('%', :keyword, '%'))
              )
            order by m.timestamp desc
            """)
    List<Message> searchByUserAndContent(@Param("userId") Long userId, @Param("keyword") String keyword);
}
