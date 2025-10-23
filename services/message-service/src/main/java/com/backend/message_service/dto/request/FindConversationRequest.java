package com.backend.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO để tìm hoặc tạo một cuộc trò chuyện giữa hai người dùng.
 */
@Getter
@Setter
public class FindConversationRequest {
    private Long user1Id;
    private Long user2Id;
}