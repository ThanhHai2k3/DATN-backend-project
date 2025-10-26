package com.backend.profileservice.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerRequest {
    private String name;
    private String position;
    private UUID companyId; // optional – null nếu muốn tạo company mới
}
