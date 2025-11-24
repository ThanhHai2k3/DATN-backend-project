package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.student.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.student.ProjectResponse;
import com.backend.profileservice.entity.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectCreateRequest request);

    ProjectResponse toResponse(Project entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Project entity, ProjectUpdateRequest request);
}
