package com.backend.authservice.dto.admin;

import com.backend.authservice.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {
    private Role role;
}
