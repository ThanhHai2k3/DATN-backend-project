package com.backend.cv_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CvDetailDto {

    private Long id;
    private UUID studentId;
    private String cvName;
    private String cvUrl;
    private boolean isDefault;
}
