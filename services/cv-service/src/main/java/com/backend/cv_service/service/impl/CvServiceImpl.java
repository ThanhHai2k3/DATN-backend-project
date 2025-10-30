package com.backend.cv_service.service.impl;

import com.backend.cv_service.dto.CvDetailDto;
import com.backend.cv_service.dto.CvSummaryDto;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.repository.*;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.service.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @Override
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

    @Override
    public List<CvSummaryDto> findAllByStudentId(UUID studentId) {
        return null;
    }

    @Override
    public CvDetailDto findCvDetailById(Long cvId, UUID studentId) {

        return null;
    }

    @Override
    public CvSummaryDto updateCvName(Long cvId, UUID studentId, String newCvName) {
        return null;
    }

    @Override
    public void deleteCv(Long cvId, UUID studentId) {

    }

    @Override
    public void setDefaultCv(Long cvId, UUID studentId) {

    }

    @Override
    public Object getStructuredDataForMatching(Long cvId) {
        return null;
    }
}

