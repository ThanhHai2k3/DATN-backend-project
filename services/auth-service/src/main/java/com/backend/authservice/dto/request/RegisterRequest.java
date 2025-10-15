package com.backend.authservice.dto.request;

import com.backend.authservice.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private Role role = Role.STUDENT;
}
