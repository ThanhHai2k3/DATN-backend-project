package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.student.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.student.ProjectResponse;
import com.backend.profileservice.entity.Project;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.ProjectMapper;
import com.backend.profileservice.repository.ProjectRepository;
import com.backend.profileservice.repository.StudentRepository;
import com.backend.profileservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final StudentRepository studentRepository;

    @Override
    public ProjectResponse create(UUID userId, ProjectCreateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Project project = projectMapper.toEntity(request);
        project.setStudent(student);

        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Override
    public ProjectResponse update(UUID userId, UUID projectId, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        projectMapper.updateEntity(project, request);

        Project saved = projectRepository.save(project);
        return projectMapper.toResponse(saved);
    }

    @Override
    public List<ProjectResponse> getAllByStudent(UUID userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return projectRepository.findByStudentId(student.getId())
                .stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID userId, UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        projectRepository.delete(project);
    }
}
