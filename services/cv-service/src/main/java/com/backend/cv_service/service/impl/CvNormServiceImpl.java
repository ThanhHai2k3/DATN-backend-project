package com.backend.cv_service.service.impl;

import com.backend.cv_service.dto.CvNlpResultDto;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.entity.CvNorm;
import com.backend.cv_service.repository.CvNormRepository;
import com.backend.cv_service.repository.CVRepository;
import com.backend.cv_service.service.CvNormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class CvNormServiceImpl implements CvNormService {

    private final CVRepository cvRepository;
    private final CvNormRepository cvNormRepository;

    public CvNormServiceImpl(CVRepository cvRepository,
                             CvNormRepository cvNormRepository) {
        this.cvRepository = cvRepository;
        this.cvNormRepository = cvNormRepository;
    }

    @Override
    @Transactional
    public void upsertFromNlpResult(Long cvId, CvNlpResultDto result) {
        System.out.println(111111);
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new IllegalArgumentException("CV not found: " + cvId));


        CvNorm cvNorm = cvNormRepository.findById(cvId)
                .orElseGet(() -> {
                    CvNorm n = new CvNorm();
                    n.setCv(cv);
//                    n.setCvId(cvId);
                    return n;
                });


        CvNlpResultDto.EducationPart edu = result.getEducation();
        if (edu != null) {
            cvNorm.setEducationLevel(edu.getLevel());
            cvNorm.setEducationMajors(
                    edu.getMajors() != null ? edu.getMajors() : Collections.emptyList()
            );
        } else {
            cvNorm.setEducationLevel(null);
            cvNorm.setEducationMajors(Collections.emptyList());
        }


        CvNlpResultDto.ExperiencePart exp = result.getExperience();
        if (exp != null) {
            cvNorm.setYearsTotal(exp.getYearsTotal());
            cvNorm.setExperienceTitles(
                    exp.getTitles() != null ? exp.getTitles() : Collections.emptyList()
            );
            cvNorm.setExperienceAreas(
                    exp.getAreas() != null ? exp.getAreas() : Collections.emptyList()
            );
        } else {
            cvNorm.setYearsTotal(null);
            cvNorm.setExperienceTitles(Collections.emptyList());
            cvNorm.setExperienceAreas(Collections.emptyList());
        }

        cvNorm.setSkillsNorm(
                result.getSkills() != null ? result.getSkills() : Collections.emptyList()
        );

        cvNorm.setModelVersion(result.getModelVersion());
        cvNorm.setProcessedAt(OffsetDateTime.now());

        cvNormRepository.save(cvNorm);

        // optional
//        cv.setNlpStatus("SUCCESS");
//        cv.setProcessedAt(OffsetDateTime.now());
        cvRepository.save(cv);
    }
}
