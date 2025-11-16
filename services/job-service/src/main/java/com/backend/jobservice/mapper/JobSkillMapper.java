package com.backend.jobservice.mapper;

import com.backend.jobservice.dto.request.JobSkillRequest;
import com.backend.jobservice.dto.response.JobSkillResponse;
import com.backend.jobservice.entity.JobSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobSkillMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "internshipPost", ignore = true) // set trong service
    @Mapping(target = "skillId", expression = "java( java.util.UUID.fromString(dto.getSkillId()) )")
    JobSkill toEntity(JobSkillRequest dto);

    @Mapping(target = "skillName", ignore = true) //set sau khi gọi skill-service
    JobSkillResponse toResponse(JobSkill entity);
}
