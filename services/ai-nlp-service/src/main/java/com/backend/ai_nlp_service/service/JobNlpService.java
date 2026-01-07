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

    private final MapboxGeocodingService mapboxGeocodingService;
    private final ProvinceResolver provinceResolver;

    public ProcessPostResponse processJob(ProcessPostRequest req) {

        List<String> skillsNorm = buildSkillsNorm(req);

        Double lat = null;
        Double lon = null;
        String province = null;

        String rawLocation = req.getLocation();

        if (StringUtils.hasText(rawLocation)) {
            MapboxGeocodingService.GeocodeResult geo = mapboxGeocodingService.geocode(rawLocation);
            if (geo != null) {
                lat = geo.lat();
                lon = geo.lon();
            }
        }

        if (lat != null && lon != null) {
            var hit = provinceResolver.resolve(lat, lon);
            if (hit != null) {
                province = hit.provinceName();
            }
        }

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

    private List<String> buildSkillsNorm(ProcessPostRequest req) {

        Set<String> merged = new LinkedHashSet<>();

        String text = joinNonBlank(req.getTitle(), req.getPosition(), req.getDescription());
        if (StringUtils.hasText(text)) {
            merged.addAll(SkillExtractor.extractSkills(text));
        }

        if (req.getSkills() != null) {
            for (JobSkillRequest s : req.getSkills()) {
                if (s == null) continue;

                String manual = s.getSkillName();
                if (!StringUtils.hasText(manual)) continue;

                List<String> mapped = SkillExtractor.extractSkills(manual);

                if (!mapped.isEmpty()) {
                    merged.addAll(mapped);
                } else {
                    merged.add(manual.trim());
                }
            }
        }

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
