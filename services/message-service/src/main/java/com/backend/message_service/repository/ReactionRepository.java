package com.backend.message_service.repository;

import com.backend.message_service.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * Tìm tất cả các reaction của một tin nhắn cụ thể.
     *
     * @param messageId ID của tin nhắn.
     * @return danh sách các reaction.
     */
    List<Reaction> findByMessageId(Long messageId);
    Optional<Reaction> findByMessageIdAndUserId(Long messageId, UUID userId);
}