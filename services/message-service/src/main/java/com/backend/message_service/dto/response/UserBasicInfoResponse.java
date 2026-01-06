package com.backend.message_service.dto.response;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicInfoResponse {
    private UUID userId;
    private String fullName;
    private String avatarUrl;
}
