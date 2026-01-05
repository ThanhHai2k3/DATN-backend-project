package com.backend.applyingservice.dto.request;

import com.backend.applyingservice.enums.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    private ApplicationStatus status;
    private String note;
}