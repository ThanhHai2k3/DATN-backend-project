package com.backend.profileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    OK("PRO_000", "Success", HttpStatus.OK),
    PROFILE_UPSERTED("PRO_001", "Profile saved", HttpStatus.OK),
    SKILLS_UPDATED("PRO_002", "Skills updated", HttpStatus.OK),
    EDU_UPDATED("PRO_003", "Educations updated", HttpStatus.OK),
    EXP_UPDATED("PRO_004", "Experiences updated", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
