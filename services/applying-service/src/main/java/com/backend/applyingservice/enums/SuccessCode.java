package com.backend.applyingservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    APPLICATION_SUBMITTED("APP_200", "Application submitted successfully", HttpStatus.CREATED),
    APPLICATION_FETCHED("APP_201", "Application details fetched successfully", HttpStatus.OK),
    APPLICATION_LIST_FETCHED("APP_202", "List of applications fetched successfully", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}