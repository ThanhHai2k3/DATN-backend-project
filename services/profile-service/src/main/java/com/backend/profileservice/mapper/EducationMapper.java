package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.student.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.student.EducationResponse;
import com.backend.profileservice.entity.Education;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEntity(EducationCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Education entity, EducationUpdateRequest request);

    EducationResponse toResponse(Education entity);
}