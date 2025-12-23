package com.backend.ai_nlp_service.service;

import com.backend.ai_nlp_service.dto.JobSkillRequest;
import com.backend.ai_nlp_service.dto.ProcessPostRequest;
import com.backend.ai_nlp_service.dto.ProcessPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobNlpService {

    private final MapboxGeocodingService mapboxGeocodingService; // nếu bạn không dùng Mapbox nữa thì bỏ field này + block geocode
    private final ProvinceResolver provinceResolver;             // offline GADM resolver (bean)

    public ProcessPostResponse processJob(ProcessPostRequest req) {

        // ===== 1) Skills =====
        List<String> skillsNorm = buildSkillsNorm(req);

        // ===== 2) Location (lat/lon + province) =====
        Double lat = null;
        Double lon = null;
        String province = null;

        String rawLocation = req.getLocation();

        // 2.1 Geocode -> lat/lon (Mapbox) (nếu bạn đã có sẵn lat/lon offline thì thay bằng dữ liệu offline)
        if (StringUtils.hasText(rawLocation)) {
            MapboxGeocodingService.GeocodeResult geo = mapboxGeocodingService.geocode(rawLocation);
            if (geo != null) {
                lat = geo.lat();
                lon = geo.lon();
            }
        }

        // 2.2 lat/lon -> province (GADM offline)
        if (lat != null && lon != null) {
            var hit = provinceResolver.resolve(lat, lon);
            if (hit != null) {
                province = hit.provinceName();
            }
        }

        // ===== 3) Build response =====
        return ProcessPostResponse.builder()
                .postId(req.getPostId())

                .skillsNorm(skillsNorm)

                .workModesNorm(StringUtils.hasText(req.getWorkMode()) ? List.of(req.getWorkMode()) : null)
                .locationsNorm(province == null ? null : List.of(province))

                .lat(lat)
                .lon(lon)

                .modelVersion("job-rule-based-0.2.0")
                .processedAt(Instant.now())
                .build();
    }

    // =========================
    // SKILLS NORMALIZATION
    // =========================
    private List<String> buildSkillsNorm(ProcessPostRequest req) {

        Set<String> merged = new LinkedHashSet<>();

        // A) Extract từ text (title/position/description)
        String text = joinNonBlank(req.getTitle(), req.getPosition(), req.getDescription());
        if (StringUtils.hasText(text)) {
            merged.addAll(SkillExtractor.extractSkills(text)); // extractSkills tự normalize + map alias -> canonical
        }

        // B) Merge skills employer nhập tay
        if (req.getSkills() != null) {
            for (JobSkillRequest s : req.getSkills()) {
                if (s == null) continue;

                String manual = s.getSkillName(); // ✅ đúng theo code bạn
                if (!StringUtils.hasText(manual)) continue;

                // map alias -> canonical bằng extractor (extractSkills tự normalize)
                List<String> mapped = SkillExtractor.extractSkills(manual);

                if (!mapped.isEmpty()) {
                    merged.addAll(mapped);
                } else {
                    // fallback nếu dict chưa có
                    merged.add(manual.trim());
                }
            }
        }

        // C) Output stable list
        return merged.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private String joinNonBlank(String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(". "));
    }
}
