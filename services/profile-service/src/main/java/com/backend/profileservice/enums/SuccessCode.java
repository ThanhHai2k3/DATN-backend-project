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

    COMPANY_CREATED("COM_001", "Company created successfully", HttpStatus.CREATED),
    COMPANY_UPDATED("COM_002", "Company updated successfully", HttpStatus.OK),
    COMPANY_FETCHED("COM_003", "Company fetched successfully", HttpStatus.OK),
    COMPANY_LIST_FETCHED("COM_004", "Companies fetched successfully", HttpStatus.OK),
    COMPANY_DELETED("COM_005", "Company deleted", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
