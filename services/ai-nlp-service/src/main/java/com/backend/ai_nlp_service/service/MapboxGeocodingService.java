package com.backend.ai_nlp_service.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapboxGeocodingService {

    private final RestClient mapboxRestClient;

    @Value("${mapbox.token}")
    private String token;

    public GeocodeResult geocode(String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

        MapboxResponse resp = mapboxRestClient.get()
                .uri(uri -> uri.path("/{q}.json")
                        .queryParam("access_token", token)
                        .queryParam("country", "VN")
                        .queryParam("language", "vi")
                        .queryParam("types", "place,locality")
                        .queryParam("autocomplete", false)
                        .queryParam("limit", 5)
                        .build(query))
                .retrieve()
                .body(MapboxResponse.class);


        if (resp == null || resp.features() == null || resp.features().isEmpty()) return null;

        Feature f = resp.features().getFirst();


        double lon = f.center().get(0);
        double lat = f.center().get(1);

        return new GeocodeResult(
                f,
                lat,
                lon
        );
    }



    public record GeocodeResult(
            Feature feature,
            double lat,
            double lon
    ) {}


    public record MapboxResponse(List<Feature> features) {}

    public record Feature(
            @JsonProperty("place_name") String placeName,
            List<Double> center,
            List<ContextItem> context
    ) {}
    public record ContextItem(
            String id,
            String text
    ) {}

}
