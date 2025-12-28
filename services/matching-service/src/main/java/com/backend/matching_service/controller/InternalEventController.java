package com.backend.matching_service.controller;

import com.backend.matching_service.dto.ingest.CvNormUpdatedEvent;
import com.backend.matching_service.dto.ingest.JobNormUpdatedEvent;
import com.backend.matching_service.service.NormSnapshotIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final NormSnapshotIngestService ingest;

    @PostMapping("/cv-norm-updated")
    public ResponseEntity<Void> onCvNormUpdated(@RequestBody CvNormUpdatedEvent event) {
        ingest.upsertCv(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/job-norm-updated")
    public ResponseEntity<Void> onJobNormUpdated(@RequestBody JobNormUpdatedEvent event) {
        ingest.upsertJob(event);
        return ResponseEntity.ok().build();
    }
}
