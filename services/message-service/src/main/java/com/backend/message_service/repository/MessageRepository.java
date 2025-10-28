package com.backend.message_service.repository;

import com.backend.message_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Tìm tất cả các tin nhắn trong một cuộc trò chuyện,
     * sắp xếp theo thời gian gửi và hỗ trợ phân trang.
     *
     * @param conversationId ID của cuộc trò chuyện.
     * @param pageable       Đối tượng chứa thông tin phân trang (trang số mấy, bao nhiêu tin nhắn mỗi trang).
     * @return một trang (Page) chứa danh sách các tin nhắn.
     */
    Page<Message> findByConversationIdOrderBySentAtDesc(Long conversationId, Pageable pageable);
}