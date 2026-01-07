package com.backend.profileservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreateRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    private String industry;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String address;
    private String companySize;
}
