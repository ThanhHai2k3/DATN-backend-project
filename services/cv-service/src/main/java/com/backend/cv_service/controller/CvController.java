package com.backend.cv_service.controller;

import com.backend.cv_service.dto.CvDetailDto;
import com.backend.cv_service.dto.CvSummaryDto;
import com.backend.cv_service.dto.UpdateCvNameRequest;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.util.JwtUtil; // Giả sử bạn có một lớp tiện ích để xử lý JWT
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv/v1")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    @PostMapping("/upload") // TODO: tested
    public ResponseEntity<CvSummaryDto> uploadNewCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("cvName") String cvName,
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        CvSummaryDto newCv = cvService.uploadExtractAndSaveCv(studentId, cvName, file);
        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
    }

    @GetMapping("/my-cvs") // TODO: tested
    public ResponseEntity<List<CvSummaryDto>> getMyCVs(
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        List<CvSummaryDto> cvs = cvService.findAllByStudentId(studentId);
        return ResponseEntity.ok(cvs);
    }

    @GetMapping("/{cvId}") // TODO: test lại, bug studentId=null sẽ hết
    public ResponseEntity<CvDetailDto> getCvById(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        CvDetailDto cvDetail = cvService.findCvDetailById(cvId, studentId);
        return ResponseEntity.ok(cvDetail);
    }

    @PutMapping("/{cvId}") // TODO: test lại
    public ResponseEntity<CvSummaryDto> updateCvName(
            @PathVariable("cvId") Long cvId,
            @RequestBody UpdateCvNameRequest request,
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        CvSummaryDto updatedCv = cvService.updateCvName(cvId, studentId, request.getNewCvName());
        return ResponseEntity.ok(updatedCv);
    }

    @DeleteMapping("/{cvId}") // TODO: test lại
    public ResponseEntity<Void> deleteCv(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        cvService.deleteCv(cvId, studentId);
        return ResponseEntity.noContent().build(); // 204
        // TODO: xử lý trường hợp xóa CV mặc định
    }

    @PutMapping("/{cvId}/set-default") // TODO: test lại
    public ResponseEntity<String> setDefaultCv(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("X-User-Id") UUID studentId
    ) {
        cvService.setDefaultCv(cvId, studentId);
        return ResponseEntity.ok("CV với id " + cvId + " đã được đặt làm mặc định.");
    }

//API nội bộ
//    @GetMapping("/{cvId}/structured-data")//TODO:chưa test
//    public ResponseEntity<Object> getStructuredDataForMatching(@PathVariable("cvId") Long cvId) {
//        // Lưu ý: Cần có cơ chế bảo mật để đảm bảo chỉ các service nội bộ mới gọi được API này.
//        Object structuredData = cvService.getStructuredDataForMatching(cvId);
//        return ResponseEntity.ok(structuredData);
//    }
}