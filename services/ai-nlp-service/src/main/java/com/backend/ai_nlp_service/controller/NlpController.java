package com.backend.ai_nlp_service.controller;

import com.backend.ai_nlp_service.dto.CvNlpResult;
import com.backend.ai_nlp_service.dto.ProcessCvRequest;
import com.backend.ai_nlp_service.service.NlpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nlp/v1")
@RequiredArgsConstructor
public class NlpController {

    private final NlpService nlpService;

    @PostMapping("/process-cv")
    public ResponseEntity<CvNlpResult> processCv(@RequestBody ProcessCvRequest request) {
        CvNlpResult result = nlpService.processCv(request);
        return ResponseEntity.ok(result);
    }
}
