package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentSkillResponse;
import com.backend.profileservice.entity.StudentSkill;
import com.backend.profileservice.enums.SkillLevel;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface StudentSkillMapper {

    default UUID mapStringToUUID(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Mapping(target = "skillId",
            expression = "java(request.getSkillId() != null ? java.util.UUID.fromString(request.getSkillId()) : (java.util.UUID) null)")
    @Mapping(target = "level",
            expression =
                    "java(request.getLevel() != null ? com.backend.profileservice.enums.SkillLevel.valueOf(request.getLevel().toUpperCase()) : null)")
    StudentSkill toEntity(StudentSkillCreateRequest request);

    @Mapping(target = "skillId",
            expression = "java(entity.getSkillId() != null ? entity.getSkillId().toString() : null)")
    @Mapping(target = "level",
            expression =
                    "java(entity.getLevel() != null ? entity.getLevel().name() : null)")
    StudentSkillResponse toResponse(StudentSkill entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "level",
            expression =
                    "java(request.getLevel() != null ? com.backend.profileservice.enums.SkillLevel.valueOf(request.getLevel().toUpperCase()) : entity.getLevel())")
    void updateEntity(@MappingTarget StudentSkill entity, StudentSkillUpdateRequest request);
}
