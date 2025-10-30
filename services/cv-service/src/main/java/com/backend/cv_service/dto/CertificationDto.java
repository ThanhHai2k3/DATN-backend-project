package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificationDto {
    private String name;
    private String issueOrg;
}