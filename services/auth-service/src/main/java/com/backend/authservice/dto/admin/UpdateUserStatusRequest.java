package com.backend.authservice.dto.admin;

import com.backend.authservice.enums.AccountStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserStatusRequest {
    private AccountStatus status;
}
