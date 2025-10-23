package com.backend.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa dữ liệu để thả một cảm xúc mới vào tin nhắn.
 */
@Getter
@Setter
public class CreateReactionRequest {
    private Long messageId;
    private Long userId;
    private String reactionType;
}