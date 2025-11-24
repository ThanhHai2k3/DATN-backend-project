package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.student.StudentCreateRequest;
import com.backend.profileservice.dto.request.student.StudentUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentResponse;
import com.backend.profileservice.entity.Education;
import com.backend.profileservice.entity.Experience;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.entity.StudentSkill;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        EducationMapper.class,
        ExperienceMapper.class,
        ProjectMapper.class,
        SocialLinkMapper.class,
        StudentSkillMapper.class
})
public interface StudentMapper {

    @Mapping(target = "gender",
            expression = "java(Gender.valueOf(request.getGender().toUpperCase()))")
    Student toEntity(StudentCreateRequest request);

    @Mapping(target = "gender", expression = "java(entity.getGender() != null ? entity.getGender().name() : null)")
    StudentResponse toResponse(Student entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "gender",
            expression = "java(request.getGender() != null ? Gender.valueOf(request.getGender().toUpperCase()) : student.getGender())")
    void updateEntity(@MappingTarget Student student, StudentUpdateRequest request);
}
