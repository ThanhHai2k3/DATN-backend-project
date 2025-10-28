package com.backend.profileservice.dto.response;

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
    private String position;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    private CompanyResponse company;
    private Instant createdAt;
    private Instant updatedAt;
}
