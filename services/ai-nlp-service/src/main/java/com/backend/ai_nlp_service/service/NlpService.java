package com.backend.ai_nlp_service.service;

import com.backend.ai_nlp_service.dto.CvNlpResult;
import com.backend.ai_nlp_service.dto.ProcessCvRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NlpService {
    //hardcore
    private static final List<String> KNOWN_SKILLS =
            Arrays.asList(
                    "java", "spring", "spring boot", "postgresql", "sql",
                    "python", "django", "javascript", "react", "docker"
            );


//    public CvNlpResult processCv(ProcessCvRequest request) {
//        String text = request.getRawText();
//        if (text == null) {
//            text = "";
//        }
//
//        String lower = text.toLowerCase(Locale.ROOT);
//
//
//        List<String> skills = new ArrayList<>();
//        for (String s : KNOWN_SKILLS) {
//            if (lower.contains(s)) {
//
//                skills.add(capitalizeSkill(s));
//            }
//        }
//
//
//        Double yearsTotal = extractYearsOfExperience(lower);
//
//
//        List<String> titles = new ArrayList<>();
//        if (lower.contains("backend")) {
//            titles.add("Backend Developer");
//        }
//        if (lower.contains("java")) {
//            titles.add("Java Developer");
//        }
//
//        List<String> areas = new ArrayList<>();
//        if (lower.contains("backend")) {
//            areas.add("Backend");
//        }
//        if (lower.contains("web")) {
//            areas.add("Web Development");
//        }
//
//        String level = null;
//        List<String> majors = new ArrayList<>();
//        if (lower.contains("đại học") || lower.contains("university")) {
//            level = "bachelor";
//        }
//
//
//        CvNlpResult result = new CvNlpResult();
//        result.setCvId(request.getCvId());
//        result.setSkills(skills);
//
//        CvNlpResult.ExperiencePart exp = new CvNlpResult.ExperiencePart();
//        exp.setYearsTotal(yearsTotal);
//        exp.setTitles(titles);
//        exp.setAreas(areas);
//        result.setExperience(exp);
//
//        CvNlpResult.EducationPart edu = new CvNlpResult.EducationPart();
//        edu.setLevel(level);
//        edu.setMajors(majors);
//        result.setEducation(edu);
//
//        result.setModelVersion("dummy-0.1.0");
//
//        return result;
//    }
public CvNlpResult processCv(ProcessCvRequest request) {
    String text = request.getRawText();
    if (text == null) {
        text = "";
    }

    String lower = text.toLowerCase(Locale.ROOT);

    // ✅ Dùng PythonSkillExtractor để lấy skills từ CV
    List<String> skills = SkillExtractor.extractSkills(text);

    // Giữ nguyên logic cũ cho kinh nghiệm
    Double yearsTotal = extractYearsOfExperience(lower);

    List<String> titles = new ArrayList<>();
    if (lower.contains("backend")) {
        titles.add("Backend Developer");
    }
    if (lower.contains("java")) {
        titles.add("Java Developer");
    }

    List<String> areas = new ArrayList<>();
    if (lower.contains("backend")) {
        areas.add("Backend");
    }
    if (lower.contains("web")) {
        areas.add("Web Development");
    }

    String level = null;
    List<String> majors = new ArrayList<>();
    if (lower.contains("đại học") || lower.contains("university")) {
        level = "bachelor";
    }

    CvNlpResult result = new CvNlpResult();
    result.setCvId(request.getCvId());
    result.setSkills(skills);

    CvNlpResult.ExperiencePart exp = new CvNlpResult.ExperiencePart();
    exp.setYearsTotal(yearsTotal);
    exp.setTitles(titles);
    exp.setAreas(areas);
    result.setExperience(exp);

    CvNlpResult.EducationPart edu = new CvNlpResult.EducationPart();
    edu.setLevel(level);
    edu.setMajors(majors);
    result.setEducation(edu);

    result.setModelVersion("rule-based-0.2.0"); // đổi tên version cho dễ debug

    return result;
}


    private Double extractYearsOfExperience(String text) {
        if (text == null || text.isEmpty()) return null;

        Pattern p = Pattern.compile(
                "(\\d+(?:\\.\\d+)?)\\s*\\+?\\s*(năm|year|years)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher m = p.matcher(text);

        Double maxYears = null;

        while (m.find()) {
            try {
                double years = Double.parseDouble(m.group(1));
                if (maxYears == null || years > maxYears) {
                    maxYears = years;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return maxYears;
    }


    private String capitalizeSkill(String s) {
        if (s == null || s.isEmpty()) return s;
        if (s.equalsIgnoreCase("sql")) return "SQL";
        if (s.equalsIgnoreCase("java")) return "Java";
        if (s.equalsIgnoreCase("python")) return "Python";
        if (s.equalsIgnoreCase("docker")) return "Docker";
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }
}
