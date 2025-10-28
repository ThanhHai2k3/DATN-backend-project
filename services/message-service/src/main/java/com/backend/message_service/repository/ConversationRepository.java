package com.backend.message_service.repository;

import com.backend.message_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Tìm một cuộc trò chuyện dựa trên ID của hai người dùng.
     * Cách này sẽ hoạt động chính xác vì chúng ta đã có ràng buộc
     * CHECK (user1_id < user2_id) trong database.
     *
     * @param user1Id ID của người dùng thứ nhất (luôn nhỏ hơn)
     * @param user2Id ID của người dùng thứ hai (luôn lớn hơn)
     * @return một Optional chứa Conversation nếu tìm thấy.
     */
    Optional<Conversation> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);
    List<Conversation> findByUser1IdOrUser2IdOrderByUpdatedAtDesc(UUID user1Id, UUID user2Id);
}
