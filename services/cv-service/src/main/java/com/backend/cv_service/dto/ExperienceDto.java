package com.backend.cv_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class ExperienceDto {
    private String companyName;
    private String position;
    private Date startDate;
    private Date endDate;
}