package com.backend.message_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO chứa thông tin đầy đủ của một tin nhắn để hiển thị cho người dùng.
 */
@Getter
@Setter
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private UserResponse sender;
    private String content;
    private String messageType;
    private Instant sentAt;
    private List<ReactionResponse> reactions;
}
