package com.backend.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO chứa dữ liệu để thả một cảm xúc mới vào tin nhắn.
 */
@Getter
@Setter
public class CreateReactionRequest {
    private Long messageId;
    private UUID userId;//todo: sẽ bị bỏ đi nhé
    private String reactionType;
}