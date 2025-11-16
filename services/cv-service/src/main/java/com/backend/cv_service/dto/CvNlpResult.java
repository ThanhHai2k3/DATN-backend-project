package com.backend.cv_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CvNlpResult {

    private Long cvId;

    private List<String> skills;

    private ExperiencePart experience;

    private EducationPart education;

    private String modelVersion;

    @Data
    public static class ExperiencePart {
        private Double yearsTotal;
        private List<String> titles;
        private List<String> areas;
    }

    @Data
    public static class EducationPart {
        /** none | associate | bachelor | master | phd | other */
        private String level;
        private List<String> majors;
    }
}
