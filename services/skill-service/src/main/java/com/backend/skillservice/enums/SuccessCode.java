package com.backend.skillservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    CATEGORY_CREATED("SK_101", "Category created", HttpStatus.CREATED),
    CATEGORY_UPDATED("SK_102", "Category updated", HttpStatus.OK),
    GET_SUCCESS("SK_103", "Get success", HttpStatus.OK),
    CATEGORY_DELETED("SK_104", "Category deleted", HttpStatus.OK),
    SKILL_CREATED("SK_105", "Skill created", HttpStatus.CREATED),
    SKILL_UPDATED("SK_106", "Skill updated", HttpStatus.OK),
    SKILL_DELETED("SK_107", "Skill deleted", HttpStatus.OK),

    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}