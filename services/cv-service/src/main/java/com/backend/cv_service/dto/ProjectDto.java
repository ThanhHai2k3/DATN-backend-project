package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private String projectName;
    private String projectUrl;
}