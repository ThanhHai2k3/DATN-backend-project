package com.backend.message_service.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO chứa thông tin công khai của một người dùng.
 * Dùng để nhúng vào các DTO khác.
 */
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;
}