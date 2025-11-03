package com.backend.cv_service.service.impl;

import com.backend.cv_service.dto.*;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.entity.Skill;
import com.backend.cv_service.exception.ResourceNotFoundException;
import com.backend.cv_service.repository.*;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvServiceImpl implements CvService {
    private final CVRepository cvRepository;
    private final CertificationRepository certificationRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final S3FileStorageService s3FileStorageService;
    private static final Logger log = LoggerFactory.getLogger(S3FileStorageService.class);

    @Override
    @Transactional
    public CvSummaryDto uploadAndSaveCv(UUID studentId, String cvName, MultipartFile file) {
        String fileUrl;
        try{
            fileUrl = s3FileStorageService.storeFile(file, "cvs/");
        }
        catch (IOException e){
            throw new RuntimeException("Lỗi khi lưu file: " + file.getOriginalFilename(), e);
        }

        boolean isFirstCv = !cvRepository.existsByStudentId(studentId);
        CV newCv = new CV();
        newCv.setStudentId(studentId);
        newCv.setCvName(cvName);
        newCv.setCvUrl(fileUrl);
        newCv.setDefault(isFirstCv);

        CV savedCv = cvRepository.save(newCv);
        return mapToCvSummaryDto(savedCv);
    }

    private CvSummaryDto mapToCvSummaryDto(CV cv) {
        return CvSummaryDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .build();
    }
    private CvDetailDto mapToCvDetailDto(CV cv){
        return CvDetailDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .educations(cv.getEducations().stream().map(
                        x -> EducationDto.builder()
                                .universityName(x.getUniversityName())
                                .major(x.getMajor())
                                .originalGpa(x.getOriginalGpa())
                                .gpaScale(x.getGpaScale())
                                .build())
                        .collect(Collectors.toList()))
                .experiences(cv.getExperiences().stream().map(
                        x-> ExperienceDto.builder()
                                .companyName(x.getCompanyName())
                                .position(x.getPosition())
                                .startDate(x.getStartDate())
                                .endDate(x.getEndDate())
                                .build())
                        .collect(Collectors.toList()))
                .certifications(cv.getCertifications().stream().map(
                        x-> CertificationDto.builder()
                                .name(x.getName())
                                .issueOrg(x.getIssueOrg())
                                .build())
                                .collect(Collectors.toList())
                        )
                .projects(cv.getProjects().stream().map(
                        x->ProjectDto.builder()
                                .projectName(x.getProjectName())
                                .projectUrl(x.getProjectUrl())
                                .build())
                                .collect(Collectors.toList())
                )
                .skills(cv.getSkills().stream()
                        .map(Skill::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvSummaryDto> findAllByStudentId(UUID studentId) {
        List<CV> cvs = cvRepository.findByStudentId(studentId);
        return cvs.stream()
                .map(this::mapToCvSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CvDetailDto findCvDetailById(Long cvId, UUID studentId) {
//        CV cv = cvRepository.findById(cvId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy CV"));
        CV cv = cvRepository.findDetailedByIdAndStudentId(cvId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV hoặc bạn không có quyền xem CV này."));
//        if(!cv.getStudentId().equals(studentId)) throw new SecurityException("Bạn không có quyền truy cập CV này");
        return mapToCvDetailDto(cv);
    }

//    @Override
//    @Transactional
//    public CvDetailDto findDetailedByIdAndStudentId(Long cvId, UUID studentId) {
//        CV cv = cvRepository.findById(cvId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy CV"));
//        if(!cv.getStudentId().equals(studentId)) throw new SecurityException("Bạn không có quyền truy cập CV này");
//        return mapToCvDetailDto(cv);
//    }

    @Override
    @Transactional
    public CvSummaryDto updateCvName(Long cvId, UUID studentId, String newCvName) {

        CV cv = cvRepository.findById(cvId)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy CV"));
        if(!cv.getStudentId().equals(studentId)){
            throw new SecurityException("CV này không phải của bạn!");
        }
        cv.setCvName(newCvName);
        CV savedCV = cvRepository.save(cv);
        return mapToCvSummaryDto(savedCV);
    }

    @Override
    @Transactional
    public void deleteCv(Long cvId, UUID studentId) {

        CV cv = cvRepository.findByIdAndStudentId(cvId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV hoặc bạn không có quyền xóa CV này."));


        try {
            s3FileStorageService.deleteFile(cv.getCvUrl());
        } catch (Exception e) {
            // Ghi log lỗi xóa file, nhưng không nên dừng việc xóa trong DB
            log.warn("Không thể xóa file trên S3: {}", cv.getCvUrl(), e);
        }

        cvRepository.delete(cv);
    }

    @Override
    @Transactional
    public void setDefaultCv(Long cvId, UUID studentId) {
        List<CV> cvList = cvRepository.findByStudentId(studentId);
        for(CV x:cvList){
            x.setDefault(false);
        }
        CV cv = cvRepository.findById(cvId).orElseThrow(()-> new ResourceNotFoundException("CV không tồn tại"));
        cv.setDefault(true);
        cvRepository.saveAll(cvList);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStructuredDataForMatching(Long cvId) {

        CV cv = cvRepository.findDetailedById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV với id: " + cvId));

        return mapToMatchingDataDto(cv);
    }
    private MatchingDataDto mapToMatchingDataDto(CV cv) {
        Set<String> skills = cv.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        List<EducationData> educations = cv.getEducations().stream()
                .map(e -> EducationData.builder()
                        .major(e.getMajor())
                        .normalizedGpa(e.getNormalizedGpa())
                        .build())
                .collect(Collectors.toList());

        List<ExperienceData> experiences = cv.getExperiences().stream()
                .map(e -> ExperienceData.builder()
                        .position(e.getPosition())
                        // TODO: Thêm logic tính toán số tháng kinh nghiệm ở đây
                        // .durationInMonths(ChronoUnit.MONTHS.between(e.getStartDate(), e.getEndDate()))
                        .build())
                .collect(Collectors.toList());

        return MatchingDataDto.builder()
                .cvId(cv.getId())
                .studentId(cv.getStudentId())
                .skills(skills)
                .educations(educations)
                .experiences(experiences)
                .build();
    }
}

