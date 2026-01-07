package com.backend.applyingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerDashboardStatsDto {
    private long totalApplications;
    private long newApplications;
}