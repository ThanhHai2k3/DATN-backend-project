package com.backend.message_service.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin của một biểu tượng cảm xúc đã được thả.
 */
@Getter
@Setter
public class ReactionResponse {
    private Long id;
    private Long userId;
    private String reactionType;
    private Long messageId;
}