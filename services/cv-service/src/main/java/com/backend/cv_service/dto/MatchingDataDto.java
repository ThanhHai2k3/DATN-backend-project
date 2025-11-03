package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DTO này được thiết kế ĐẶC BIỆT để phục vụ cho matching-service.
 * Nó chứa dữ liệu đã được tính toán và chuẩn hóa.
 */
@Data
@Builder
public class MatchingDataDto {

    private Long cvId;
    private UUID studentId;
    private Set<String> skills;
    private List<EducationData> educations;
    private List<ExperienceData> experiences;

    // private Integer totalExperienceMonths;
    // private BigDecimal highestNormalizedGpa;
}

