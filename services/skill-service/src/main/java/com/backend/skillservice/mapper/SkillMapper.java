package com.backend.skillservice.mapper;

import com.backend.skillservice.dto.request.CreateSkillRequest;
import com.backend.skillservice.dto.request.UpdateSkillRequest;
import com.backend.skillservice.dto.response.SkillResponse;
import com.backend.skillservice.entity.Skill;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {SkillCategoryMapper.class})
public interface SkillMapper {

    @Mapping(target = "category", source = "category")
    SkillResponse toResponse(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true) // set ở service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(CreateSkillRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)   // update category trong service
    void updateEntityFromDto(UpdateSkillRequest dto, @MappingTarget Skill entity);
}
