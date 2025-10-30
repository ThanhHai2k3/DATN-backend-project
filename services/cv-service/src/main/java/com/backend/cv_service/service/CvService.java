package com.backend.cv_service.service;

import com.backend.cv_service.dto.*;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.entity.Skill;
import com.backend.cv_service.exception.ResourceNotFoundException;
import com.backend.cv_service.repository.CVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CvService {

    @Autowired
    private CVRepository cvRepository;

    // Giả sử có một service để xử lý việc lưu file
    // @Autowired
    // private FileStorageService fileStorageService;

    @Transactional
    public CvSummaryDto uploadAndSaveCv(UUID studentId, String cvName, MultipartFile file) {
        // String fileUrl = fileStorageService.storeFile(file); // Logic thật
        String fileUrl = "https://your-storage.com/" + file.getOriginalFilename(); // Logic giả

        // Kiểm tra xem đây có phải CV đầu tiên không để set default
        boolean isFirstCv = !cvRepository.existsByStudentId(studentId);

        CV newCv = new CV();
        newCv.setStudentId(studentId);
        newCv.setCvName(cvName);
        newCv.setCvUrl(fileUrl);
        newCv.setDefault(isFirstCv);

        CV savedCv = cvRepository.save(newCv);
        return mapToCvSummaryDto(savedCv);
    }

    public List<CvSummaryDto> findAllByStudentId(UUID studentId) {
        return cvRepository.findByStudentId(studentId).stream()
                .map(this::mapToCvSummaryDto)
                .collect(Collectors.toList());
    }

    public CvDetailDto findCvDetailById(Long cvId, UUID studentId) {
        CV cv = cvRepository.findByIdAndStudentId(cvId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found with id: " + cvId));
        return mapToCvDetailDto(cv);
    }

    @Transactional
    public CvSummaryDto updateCvName(Long cvId, UUID studentId, String newCvName) {
        CV cv = cvRepository.findByIdAndStudentId(cvId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found with id: " + cvId));

        cv.setCvName(newCvName);
        CV updatedCv = cvRepository.save(cv);
        return mapToCvSummaryDto(updatedCv);
    }

    @Transactional
    public void deleteCv(Long cvId, UUID studentId) {
        CV cv = cvRepository.findByIdAndStudentId(cvId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found with id: " + cvId));

        // Cần có logic xóa file vật lý ở đây
        // fileStorageService.deleteFile(cv.getCvUrl());

        cvRepository.delete(cv);
    }

    @Transactional
    public void setDefaultCv(Long cvId, UUID studentId) {
        // Đảm bảo CV mục tiêu tồn tại và thuộc về user
        if (!cvRepository.existsByIdAndStudentId(cvId, studentId)) {
            throw new ResourceNotFoundException("CV not found with id: " + cvId);
        }

        List<CV> allCvs = cvRepository.findByStudentId(studentId);
        for (CV cv : allCvs) {
            cv.setDefault(cv.getId().equals(cvId));
        }
        cvRepository.saveAll(allCvs);
    }

    public Object getStructuredDataForMatching(Long cvId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found with id: " + cvId));

        // Logic để tạo JSON cho matching service
        // Đây là nơi bạn sẽ tính toán duration_months, v.v...
        // Tạm thời trả về một DTO đơn giản
        return mapToCvDetailDto(cv); // Có thể tạo một DTO riêng cho việc matching
    }


    // ===================================================================
    // HELPER METHODS (MAPPERS)
    // Trong dự án lớn nên dùng thư viện như MapStruct
    // ===================================================================

    private CvSummaryDto mapToCvSummaryDto(CV cv) {
        return CvSummaryDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .build();
    }

    private CvDetailDto mapToCvDetailDto(CV cv) {
        return CvDetailDto.builder()
                .id(cv.getId())
                .studentId(cv.getStudentId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .skills(cv.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                // Thêm các mapper cho experiences, educations... ở đây
                .build();
    }
}