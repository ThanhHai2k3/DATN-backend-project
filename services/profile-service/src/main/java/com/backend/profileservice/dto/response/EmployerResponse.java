package com.backend.profileservice.dto.response;

import com.backend.profileservice.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private Gender gender;
    private String position;

    private boolean admin;

    private CompanyResponse company;
    private Instant createdAt;
    private Instant updatedAt;
}
