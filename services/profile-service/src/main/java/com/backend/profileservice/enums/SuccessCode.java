package com.backend.profileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    PROFILE_FETCHED("PRO_001", "Profile fetched successfully", HttpStatus.OK),
    PROFILE_AUTO_CREATED("PRO_002", "Profile auto-created successfully", HttpStatus.CREATED),
    PROFILE_UPSERTED("PRO_003", "Profile saved", HttpStatus.OK),
    SKILLS_UPDATED("PRO_004", "Skills updated", HttpStatus.OK),
    EDU_UPDATED("PRO_005", "Educations updated", HttpStatus.OK),
    EXP_UPDATED("PRO_006", "Experiences updated", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
