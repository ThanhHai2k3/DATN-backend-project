package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.student.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.student.EducationResponse;
import com.backend.profileservice.entity.Education;
import com.backend.profileservice.enums.Degree;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    @Mapping(target = "degree",
            expression = "java(request.getDegree() != null ? Degree.valueOf(request.getDegree().toUpperCase()) : null)")
    Education toEntity(EducationCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "degree",
            expression = "java(request.getDegree() != null ? Degree.valueOf(request.getDegree().toUpperCase()) : entity.getDegree())")
    void updateEntity(@MappingTarget Education entity, EducationUpdateRequest request);

    @Mapping(target = "degree",
            expression = "java(entity.getDegree() != null ? entity.getDegree().name() : null)")
    EducationResponse toResponse(Education entity);
}
