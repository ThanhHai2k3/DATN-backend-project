package com.backend.message_service.repository;

import com.backend.message_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderBySentAtDesc(Long conversationId);
//    Page<Message> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);
}