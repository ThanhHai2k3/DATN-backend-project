package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.student.studentskill.StudentSkillCreateRequest;
import com.backend.profileservice.dto.request.student.studentskill.StudentSkillUpdateRequest;
import com.backend.profileservice.dto.response.student.StudentSkillResponse;

import java.util.List;
import java.util.UUID;

public interface StudentSkillService {

    StudentSkillResponse create(UUID userId, StudentSkillCreateRequest request);

    StudentSkillResponse update(UUID userId, UUID studentSkillId, StudentSkillUpdateRequest request);

    void delete(UUID userId, UUID studentSkillId);

    List<StudentSkillResponse> getAllByStudent(UUID userId);
}
