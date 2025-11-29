package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.StudentCreateRequest;
import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        EducationMapper.class,
        ExperienceMapper.class,
        ProjectMapper.class,
        SocialLinkMapper.class,
        StudentSkillMapper.class
})
public interface StudentMapper {

    Student toEntity(StudentCreateRequest request);

    StudentResponse toResponse(Student entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Student student, StudentUpdateRequest request);
}
