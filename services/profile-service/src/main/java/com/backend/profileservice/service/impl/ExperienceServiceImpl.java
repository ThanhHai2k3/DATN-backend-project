package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.request.student.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.student.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.student.ExperienceResponse;
import com.backend.profileservice.entity.Experience;
import com.backend.profileservice.entity.Student;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.ExperienceMapper;
import com.backend.profileservice.repository.ExperienceRepository;
import com.backend.profileservice.repository.StudentRepository;
import com.backend.profileservice.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;
    private final StudentRepository studentRepository;

    @Override
    public ExperienceResponse create(UUID userId, ExperienceCreateRequest request){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Experience exp = experienceMapper.toEntity(request);
        exp.setStudent(student);

        Experience saved = experienceRepository.save(exp);
        return experienceMapper.toResponse(saved);
    }

    @Override
    public ExperienceResponse update(UUID userId, UUID experienceId, ExperienceUpdateRequest request){
        Experience exp = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new AppException(ErrorCode.EXPERIENCE_NOT_FOUND));

        if (!exp.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        experienceMapper.updateEntity(exp, request);

        Experience saved = experienceRepository.save(exp);
        return experienceMapper.toResponse(saved);
    }

    @Override
    public List<ExperienceResponse> getAllByStudent(UUID userId){
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        return experienceRepository.findByStudentId(student.getId())
                .stream()
                .map(experienceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID userId, UUID experienceId){
        Experience exp = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new AppException(ErrorCode.EXPERIENCE_NOT_FOUND));

        if (!exp.getStudent().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        experienceRepository.delete(exp);
    }
}
