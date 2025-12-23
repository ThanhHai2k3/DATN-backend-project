package com.backend.ai_nlp_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceResolver {

    private final ObjectMapper objectMapper;

    private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326); // WGS84
    private final GeoJsonReader geoJsonReader = new GeoJsonReader(gf);

    private final List<ProvincePolygon> provinces = new ArrayList<>();
    private final STRtree index = new STRtree(); // spatial index

    public record ProvinceHit(String provinceName) {}

    private record ProvincePolygon(String name, Geometry geom) {}

    @PostConstruct
    public void load() throws Exception {
        // đổi path theo nơi bạn đặt file trong resources
        ClassPathResource res = new ClassPathResource("gadm/gadm41_VNM_1.json");

        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode features = root.get("features");
            if (features == null || !features.isArray()) {
                throw new IllegalStateException("Invalid GeoJSON: missing features[]");
            }

            for (JsonNode feature : features) {
                JsonNode props = feature.get("properties");
                JsonNode geomNode = feature.get("geometry");

                if (props == null || geomNode == null) continue;

                // GADM level 1 thường có NAME_1 (tỉnh/thành)
                String name = props.path("NAME_1").asText(null);
                if (name == null) continue;

                String geomJson = geomNode.toString();
                Geometry geom = geoJsonReader.read(geomJson);

                ProvincePolygon pp = new ProvincePolygon(name, geom);
                provinces.add(pp);

                // index theo envelope để query nhanh
                index.insert(geom.getEnvelopeInternal(), pp);
            }
            index.build();
        }
    }

    public ProvinceHit resolve(double lat, double lon) {
        // JTS: Point(x=lon, y=lat)
        Point p = gf.createPoint(new Coordinate(lon, lat));

        @SuppressWarnings("unchecked")
        List<ProvincePolygon> candidates = index.query(p.getEnvelopeInternal());

        for (ProvincePolygon pp : candidates) {
            if (pp.geom.contains(p)) {
                return new ProvinceHit(pp.name);
            }
        }
        return null;
    }
}
