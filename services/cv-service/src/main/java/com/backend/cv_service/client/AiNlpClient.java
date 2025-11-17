package com.backend.cv_service.client;

import com.backend.cv_service.dto.CvNlpRequest;
import com.backend.cv_service.dto.CvNlpResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-nlp-service",
        url = "${URL_AI_NLP}"
//        url = "http://localhost:8086"
)
public interface AiNlpClient {

    @PostMapping("/api/nlp/v1/process-cv")        // path trùng với ai-nlp-service
    CvNlpResultDto processCv(@RequestBody CvNlpRequest request);
}
