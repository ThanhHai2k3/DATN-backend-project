package com.backend.profileservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyUpdateRequest {

    private String name;
    private String industry;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String address;
    private String companySize;
}

