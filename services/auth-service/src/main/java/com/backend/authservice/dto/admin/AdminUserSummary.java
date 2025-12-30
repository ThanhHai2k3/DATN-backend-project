package com.backend.authservice.dto.admin;

import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.Role;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserSummary {
    private UUID id;
    private String email;
    private Role role;
    private AccountStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
