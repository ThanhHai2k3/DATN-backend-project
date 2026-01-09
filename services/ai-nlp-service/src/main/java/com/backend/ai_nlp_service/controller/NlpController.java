package com.backend.ai_nlp_service.controller;

import com.backend.ai_nlp_service.dto.CvNlpResult;
import com.backend.ai_nlp_service.dto.ProcessCvRequest;
import com.backend.ai_nlp_service.dto.ProcessPostRequest;
import com.backend.ai_nlp_service.dto.ProcessPostResponse;
import com.backend.ai_nlp_service.service.JobNlpService;
import com.backend.ai_nlp_service.service.MapboxGeocodingService;
import com.backend.ai_nlp_service.service.NlpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nlp/v1")
@RequiredArgsConstructor
public class NlpController {

    private final NlpService nlpService;
    private final JobNlpService jobNlpService;
    private final MapboxGeocodingService mapboxGeocodingService;

    @PostMapping("/process-cv")
    public ResponseEntity<CvNlpResult> processCv(@RequestBody ProcessCvRequest request) {
        CvNlpResult result = nlpService.processCv(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/process-post")
    public ProcessPostResponse processJob(@RequestBody ProcessPostRequest request) {
        return jobNlpService.processJob(request);
    }

    @GetMapping("/geocode")
    public MapboxGeocodingService.GeocodeResult geocode(@RequestParam("q") String q) {
        System.out.println(q);
        System.out.println(mapboxGeocodingService.geocode(q));
        return mapboxGeocodingService.geocode(q);
    }
}
