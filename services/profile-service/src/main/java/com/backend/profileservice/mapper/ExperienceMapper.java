package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.student.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.student.ExperienceResponse;
import com.backend.profileservice.entity.Experience;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    Experience toEntity(ExperienceCreateRequest request);

    ExperienceResponse toResponse(Experience entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Experience entity, ExperienceUpdateRequest request);
}
