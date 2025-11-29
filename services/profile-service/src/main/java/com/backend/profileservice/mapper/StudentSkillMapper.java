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

    StudentSkill toEntity(StudentSkillCreateRequest request);

    StudentSkillResponse toResponse(StudentSkill entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget StudentSkill entity, StudentSkillUpdateRequest request);
}
