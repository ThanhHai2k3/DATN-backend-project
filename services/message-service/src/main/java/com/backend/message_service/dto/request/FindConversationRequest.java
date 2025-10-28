package com.backend.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO để tìm hoặc tạo một cuộc trò chuyện giữa hai người dùng.
 */
@Getter
@Setter
public class FindConversationRequest {
    private UUID user1Id;
    private UUID user2Id;
}