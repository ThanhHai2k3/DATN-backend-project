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

    public ProcessPostResponse processJob(ProcessPostRequest req) {

        Double lat = null;
        Double lon = null;
        String locationNorm = null;

        String rawLocation = req.getLocation();

        // 1) Normalize location -> lat/lon (Mapbox)
        System.out.println("sắp if");
        if (StringUtils.hasText(rawLocation)) {
            System.out.println("sắp chạy geocode");
            MapboxGeocodingService.GeocodeResult geo = mapboxGeocodingService.geocode(rawLocation);

            if (geo != null) {
                lat = geo.lat();
                lon = geo.lon();
                MapboxGeocodingService.Feature f = geo.feature(); // hoặc feature bạn chọn
                locationNorm = extractProvinceFromFeature(f);

//                locationNorm = geo.displayName();
            }
        }

        // 2) Build response (tạm thời chỉ fill lat/lon + meta; phần khác làm sau)
        return ProcessPostResponse.builder()
                .postId(req.getPostId())

                // skills/experience/domain/locationNorm/workModeNorm/duration/salary: để null trước
                .lat(lat)
                .lon(lon)
                .locationsNorm(
                        locationNorm == null ? null : List.of(locationNorm)
                )

                .modelVersion("mapbox-geocode-0.1.0")
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
