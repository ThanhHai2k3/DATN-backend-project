package com.backend.profileservice.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private UUID id;
    private String name;
    private String industry;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String address;
    private String companySize;
    private Instant createdAt;
    private Instant updatedAt;
}
