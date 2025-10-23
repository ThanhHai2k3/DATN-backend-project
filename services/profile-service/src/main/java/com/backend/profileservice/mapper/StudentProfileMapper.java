package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.EducationDTO;
import com.backend.profileservice.dto.ExperienceDTO;
import com.backend.profileservice.dto.StudentSkillDTO;
import com.backend.profileservice.dto.request.StudentProfileRequest;
import com.backend.profileservice.dto.response.StudentProfileResponse;
import com.backend.profileservice.entity.Education;
import com.backend.profileservice.entity.Experience;
import com.backend.profileservice.entity.StudentProfile;
import com.backend.profileservice.entity.StudentSkill;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "studentSkills", ignore = true)
    StudentProfile toEntity(StudentProfileRequest request);

    @Mapping(target = "skills", source = "studentSkills")
    StudentProfileResponse toResponse(StudentProfile entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromRequest(StudentProfileRequest request, @MappingTarget StudentProfile entity);

    // child mappers
    EducationDTO toDto(Education e);
    ExperienceDTO toDto(Experience e);

    @Mapping(target = "name", source = "skill.name")
    @Mapping(target = "category", source = "skill.category")
    @Mapping(target = "level", source = "level")
    StudentSkillDTO toDto(StudentSkill ss);

    List<EducationDTO> toEduDtos(List<Education> educations);
    List<ExperienceDTO> toExpDtos(List<Experience> experiences);
    List<StudentSkillDTO> toSkillDtos(List<StudentSkill> studentSkills);
}
