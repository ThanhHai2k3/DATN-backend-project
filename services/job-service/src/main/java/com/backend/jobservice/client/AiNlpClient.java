package com.backend.jobservice.client;

import com.backend.jobservice.dto.request.ProcessPostRequest;
import com.backend.jobservice.dto.response.ProcessPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-nlp-service",
        url = "${URL_AI_NLP}"
)
public interface AiNlpClient {

    @PostMapping("/api/nlp/v1/process-post")
    ProcessPostResponse processJob(@RequestBody ProcessPostRequest request);
}
