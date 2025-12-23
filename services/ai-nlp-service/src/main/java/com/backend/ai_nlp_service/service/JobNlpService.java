package com.backend.ai_nlp_service.service;

import com.backend.ai_nlp_service.dto.ProcessPostRequest;
import com.backend.ai_nlp_service.dto.ProcessPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobNlpService {

    private final MapboxGeocodingService mapboxGeocodingService;
    private final ProvinceResolver provinceResolver;

    public ProcessPostResponse processJob(ProcessPostRequest req) {

        String rawLocation = req.getLocation();
        Double lat = null;
        Double lon = null;

        MapboxGeocodingService.Feature feature = null;

        // 1) Geocode -> lat/lon (+ feature)
        if (StringUtils.hasText(rawLocation)) {
            MapboxGeocodingService.GeocodeResult geo = mapboxGeocodingService.geocode(rawLocation);
            if (geo != null) {
                lat = geo.lat();
                lon = geo.lon();
                feature = geo.feature();
            }
        }

        // 2) Resolve province (ưu tiên offline GADM)
        String province = null;

        if (lat != null && lon != null) {
            var hit = provinceResolver.resolve(lat, lon);
            if (hit != null) {
                province = hit.provinceName();
            }
        }

        // 3) Fallback: lấy province từ Mapbox context nếu GADM không ra
        if (province == null && feature != null) {
            province = extractProvinceFromFeature(feature);
        }

        return ProcessPostResponse.builder()
                .postId(req.getPostId())
                .lat(lat)
                .lon(lon)
                .locationsNorm(province == null ? null : List.of(province))
                .modelVersion("job-rule-based-0.1.0")
                .processedAt(Instant.now())
                .build();
    }

    private String extractProvinceFromFeature(MapboxGeocodingService.Feature f) {
        if (f.context() == null) return null;
        for (MapboxGeocodingService.ContextItem c : f.context()) {
            if (c.id() != null && c.id().startsWith("region.")) {
                return c.text();
            }
        }
        return null;
    }


}
