package com.backend.profileservice.service.impl;

import com.backend.profileservice.dto.EducationDTO;
import com.backend.profileservice.dto.ExperienceDTO;
import com.backend.profileservice.dto.StudentSkillDTO;
import com.backend.profileservice.dto.request.StudentProfileRequest;
import com.backend.profileservice.dto.response.StudentProfileResponse;
import com.backend.profileservice.entity.*;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.StudentProfileMapper;
import com.backend.profileservice.repository.*;
import com.backend.profileservice.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final StudentProfileMapper studentProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getByUserId(UUID userId){
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return studentProfileMapper.toResponse(profile);
    }

    @Override
    public StudentProfileResponse upsert(StudentProfileRequest request){
        StudentProfile profile = studentProfileRepository.findByUserId(request.getUserId())
                .orElse(null);
        if(profile == null){
            StudentProfile newProfile = studentProfileMapper.toEntity(request);
            newProfile.setUserId(request.getUserId());
            if(request.getIsVisible() != null){
                newProfile.setVisible(request.getIsVisible());
            }
            StudentProfile saved = studentProfileRepository.save(newProfile);
            return studentProfileMapper.toResponse(saved);
        }
        else{
            // Update existing profile
            profile.setFullName(request.getFullName());
            profile.setAvatarUrl(request.getAvatarUrl());
            profile.setDob(request.getDob());
            profile.setGender(request.getGender());
            profile.setAddress(request.getAddress());
            profile.setBio(request.getBio());
            profile.setCvUrl(request.getCvUrl());
            profile.setCvText(request.getCvText());
            if (request.getIsVisible() != null) {
                profile.setVisible(request.getIsVisible());
            }

            return studentProfileMapper.toResponse(profile);
        }
    }

    @Override
    public void replaceEducations(UUID userId, List<EducationDTO> dtos){
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        educationRepository.deleteByStudentId(profile.getId());

        if(dtos != null && !dtos.isEmpty()){
            for (EducationDTO dto : dtos) {
                Education education = Education.builder()
                        .student(profile)
                        .schoolName(dto.getSchoolName())
                        .major(dto.getMajor())
                        .degree(dto.getDegree())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .description(dto.getDescription())
                        .build();
                educationRepository.save(education);
            }
        }
    }

    @Override
    public void replaceExperiences(UUID userId, List<ExperienceDTO> dtos) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        experienceRepository.deleteByStudentId(profile.getId());

        if (dtos != null && !dtos.isEmpty()) {
            for (ExperienceDTO dto : dtos) {
                Experience experience = Experience.builder()
                        .student(profile)
                        .projectName(dto.getProjectName())
                        .companyName(dto.getCompanyName())
                        .role(dto.getRole())
                        .description(dto.getDescription())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .build();
                experienceRepository.save(experience);
            }
        }
    }

    @Override
    public StudentProfileResponse replaceSkills(UUID userId, List<StudentSkillDTO> dtos) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        studentSkillRepository.deleteByStudentId(profile.getId());

        if (dtos != null && !dtos.isEmpty()) {
            for (StudentSkillDTO dto : dtos) {
                Skill skill = skillRepository.findByNameIgnoreCase(dto.getName())
                        .orElseGet(() -> skillRepository.save(
                                Skill.builder()
                                        .name(dto.getName())
                                        .category(dto.getCategory())
                                        .build()
                        ));

                StudentSkillId id = new StudentSkillId(profile.getId(), skill.getId());
                StudentSkill studentSkill = StudentSkill.builder()
                        .id(id)
                        .student(profile)
                        .skill(skill)
                        .level(dto.getLevel())
                        .build();

                studentSkillRepository.save(studentSkill);
            }
        }

        return studentProfileMapper.toResponse(profile);
    }

    @Override
    public StudentProfileResponse updateVisibility(UUID userId, boolean isVisible) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        profile.setVisible(isVisible);
        return studentProfileMapper.toResponse(profile);
    }
}
