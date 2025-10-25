package com.backend.message_service.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin tóm tắt của một cuộc trò chuyện để hiển thị trong danh sách chat.
 */
@Getter
@Setter
public class ConversationResponse {
    private Long id;
    private UserResponse receiver; //cũ: participant
    private MessageResponse lastMessage;
    private long unreadCount;
}