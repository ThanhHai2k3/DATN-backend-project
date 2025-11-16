package com.backend.skillservice.mapper;

import com.backend.skillservice.dto.request.CreateCategoryRequest;
import com.backend.skillservice.dto.request.UpdateCategoryRequest;
import com.backend.skillservice.dto.response.SkillCategoryResponse;
import com.backend.skillservice.entity.SkillCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SkillCategoryMapper {

    SkillCategoryResponse toResponse(SkillCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SkillCategory toEntity(CreateCategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateCategoryRequest request, @MappingTarget SkillCategory entity);
}
