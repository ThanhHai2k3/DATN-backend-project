//package com.backend.cv_service.service;
//
//import com.backend.cv_service.dto.CvNlpResult;
//import com.backend.cv_service.entity.CV;
//import com.backend.cv_service.entity.CvNorm;
//import com.backend.cv_service.repository.CVRepository;
//import com.backend.cv_service.repository.CvNormRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.OffsetDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CvNormService {
//
//    private final CvNormRepository cvNormRepository;
//    private final CVRepository cvRepository;
//
//    /**
//     * Nhận kết quả NLP (CvNlpResult) và lưu vào bảng cv_norm.
//     * - Nếu cv_norm cho cvId này đã tồn tại → cập nhật (update).
//     * - Nếu chưa tồn tại → tạo mới (insert).
//     */
//    @Transactional
//    public CvNorm upsertFromNlpResult(CvNlpResult result) {
//        Long cvId = result.getCvId();
//        if (cvId == null) {
//            throw new IllegalArgumentException("cvId trong CvNlpResult không được null");
//        }
//
//        // 1. Lấy CV gốc (đảm bảo cvId hợp lệ)
//        CV cv = cvRepository.findById(cvId)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy CV với id = " + cvId));
//
//        // 2. Tìm CvNorm theo cvId (PK của CvNorm chính là cvId)
//        Optional<CvNorm> optional = cvNormRepository.findById(cvId);
//
//        CvNorm norm = optional.orElseGet(CvNorm::new);
//        // Quan trọng: set CV + cvId (vì dùng @MapsId)
//        norm.setCv(cv);
//        norm.setCvId(cvId);
//
//        // 3. Map data từ result sang entity CvNorm
//        mapFromResult(norm, result);
//
//        // 4. Lưu (Hibernate sẽ quyết định insert hay update)
//        return cvNormRepository.save(norm);
//    }
//
//    /** Hàm hỗ trợ: map DTO → entity */
//    private void mapFromResult(CvNorm norm, CvNlpResult result) {
//        // Skills
//        List<String> skills = distinctTrimOrEmpty(result.getSkills());
//        norm.setSkillsNorm(skills);
//
//        // Experience
//        if (result.getExperience() != null) {
//            norm.setYearsTotal(result.getExperience().getYearsTotal());
//
//            norm.setExperienceTitles(
//                    distinctTrimOrEmpty(result.getExperience().getTitles())
//            );
//            norm.setExperienceAreas(
//                    distinctTrimOrEmpty(result.getExperience().getAreas())
//            );
//        } else {
//            norm.setYearsTotal(null);
//            norm.setExperienceTitles(Collections.emptyList());
//            norm.setExperienceAreas(Collections.emptyList());
//        }
//
//        // Education
//        if (result.getEducation() != null) {
//            norm.setEducationLevel(
//                    safeString(result.getEducation().getLevel())
//            );
//            norm.setEducationMajors(
//                    distinctTrimOrEmpty(result.getEducation().getMajors())
//            );
//        } else {
//            norm.setEducationLevel(null);
//            norm.setEducationMajors(Collections.emptyList());
//        }
//
//        // Trace
//        norm.setModelVersion(safeString(result.getModelVersion()));
//        norm.setProcessedAt(OffsetDateTime.now());
//    }
//
//    /** Loại null, trim, distinct; nếu null → [] */
//    private List<String> distinctTrimOrEmpty(List<String> in) {
//        if (in == null || in.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return in.stream()
//                .filter(s -> s != null && !s.isBlank())
//                .map(String::trim)
//                .distinct()
//                .toList();
//    }
//
//    /** Nếu string null/blank → null; ngược lại trim */
//    private String safeString(String s) {
//        if (s == null || s.isBlank()) return null;
//        return s.trim();
//    }
//}
