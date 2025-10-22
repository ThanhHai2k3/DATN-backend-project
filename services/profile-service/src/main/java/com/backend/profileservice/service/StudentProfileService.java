package com.backend.profileservice.service;

import com.backend.profileservice.dto.EducationDTO;
import com.backend.profileservice.dto.ExperienceDTO;
import com.backend.profileservice.dto.StudentSkillDTO;
import com.backend.profileservice.dto.request.StudentProfileRequest;
import com.backend.profileservice.dto.response.StudentProfileResponse;

import java.util.List;
import java.util.UUID;

public interface StudentProfileService {
    StudentProfileResponse getByUserId(UUID userId);
    StudentProfileResponse upsert(StudentProfileRequest request); // tạo/sửa profile
    void replaceEducations(UUID userId, List<EducationDTO> educations);
    void replaceExperiences(UUID userId, List<ExperienceDTO> experiences);
    StudentProfileResponse replaceSkills(UUID userId, List<StudentSkillDTO> skills);
    StudentProfileResponse updateVisibility(UUID userId, boolean isVisible);
}
