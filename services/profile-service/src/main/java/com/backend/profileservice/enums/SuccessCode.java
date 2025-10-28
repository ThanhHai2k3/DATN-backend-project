package com.backend.profileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    PROFILE_FETCHED("STU_001", "Profile fetched successfully", HttpStatus.OK),
    STUDENT_PROFILE_AUTO_CREATED("STU_002", "Student profile auto-created successfully", HttpStatus.CREATED),
    PROFILE_UPSERTED("STU_003", "Profile saved", HttpStatus.OK),
    SKILLS_UPDATED("STU_004", "Skills updated", HttpStatus.OK),
    EDU_UPDATED("STU_005", "Educations updated", HttpStatus.OK),
    EXP_UPDATED("STU_006", "Experiences updated", HttpStatus.OK),

    EMPLOYER_INFO_CREATED("EMP_001", "Employer profile created successfully", HttpStatus.CREATED),
    EMPLOYER_INFO_UPDATED("EMP_002", "Employer profile updated successfully", HttpStatus.OK),
    EMPLOYER_INFO_FETCHED("EMP_003", "Employer profile fetched successfully", HttpStatus.OK),
    EMPLOYER_INFO_DELETED("EMP_004", "Employer profile deleted successfully", HttpStatus.OK),
    EMPLOYERS_LIST_FETCHED("EMP_005", "List of employers fetched successfully", HttpStatus.OK),
    EMPLOYER_PROFILE_AUTO_CREATED("EMP_006", "Employer profile auto-created successfully", HttpStatus.CREATED),

    COMPANY_INFO_CREATED("COM_001", "Company created successfully", HttpStatus.CREATED),
    COMPANY_INFO_UPDATED("COM_002", "Company updated successfully", HttpStatus.OK),
    COMPANY_INFO_FETCHED("COM_003", "Company fetched successfully", HttpStatus.OK),
    COMPANIES_LIST_FETCHED("COM_004", "List of companies fetched successfully", HttpStatus.OK),
    COMPANY_INFO_DELETED("COM_005", "Company deleted", HttpStatus.OK),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
