package com.backend.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO chung để đóng gói các sự kiện gửi qua WebSocket.
 * @param <T> Kiểu dữ liệu của payload (nội dung chính).
 */
@Getter
@Setter
@AllArgsConstructor
public class WebSocketEvent<T> {
    private String eventType; // Ví dụ: "ADD_REACTION", "REMOVE_REACTION", "RECALL_MESSAGE"
    private T payload;
}