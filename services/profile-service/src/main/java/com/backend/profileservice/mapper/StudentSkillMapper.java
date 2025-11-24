package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentSkillResponse;
import com.backend.profileservice.entity.StudentSkill;
import com.backend.profileservice.enums.SkillLevel;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentSkillMapper {

    @Mapping(target = "skillId",
            expression = "java(request.getSkillId() != null ? java.util.UUID.fromString(request.getSkillId()) : null)")
    @Mapping(target = "level",
            expression = "java(request.getLevel() != null ? SkillLevel.valueOf(request.getLevel().toUpperCase()) : null)")
    StudentSkill toEntity(StudentSkillCreateRequest request);

    @Mapping(target = "skillId", expression = "java(entity.getSkillId().toString())")
    @Mapping(target = "level",
            expression = "java(entity.getLevel() != null ? entity.getLevel().name() : null)")
    StudentSkillResponse toResponse(StudentSkill entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "level",
            expression = "java(request.getLevel() != null ? SkillLevel.valueOf(request.getLevel().toUpperCase()) : entity.getLevel())")
    void updateEntity(@MappingTarget StudentSkill entity, StudentSkillUpdateRequest request);
}
