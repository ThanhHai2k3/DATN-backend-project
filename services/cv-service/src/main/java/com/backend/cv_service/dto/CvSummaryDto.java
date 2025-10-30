package com.backend.cv_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CvSummaryDto {

    private Long id;
    private String cvName;
    private String cvUrl;
    private boolean isDefault;
}