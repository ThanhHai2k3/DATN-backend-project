package com.backend.message_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO chứa dữ liệu cần thiết để gửi một tin nhắn mới.
 */
@Getter
@Setter
public class SendMessageRequest {
    private Long conversationId;
    private UUID senderId;
    private String content;
    private String messageType; // "TEXT", "FILE", "STICKER", IMAGE, RECALLED, (VIDEO, hmmm :(( should it be here?)
}