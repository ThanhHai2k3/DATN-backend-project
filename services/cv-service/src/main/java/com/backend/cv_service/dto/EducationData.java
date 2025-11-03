package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

// SỬA LỖI: Thêm "public" vào class này
@Data
@Builder
public class EducationData {
    private String major;
    private BigDecimal normalizedGpa;
}
