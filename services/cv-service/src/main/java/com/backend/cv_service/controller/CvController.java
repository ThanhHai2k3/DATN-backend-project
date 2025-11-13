package com.backend.cv_service.controller;

import com.backend.cv_service.dto.CvDetailDto;
import com.backend.cv_service.dto.CvSummaryDto;
import com.backend.cv_service.dto.UpdateCvNameRequest;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.util.JwtUtil; // Giả sử bạn có một lớp tiện ích để xử lý JWT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv/v1")
public class CvController {

    @Autowired
    private CvService cvService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/upload")//TODO:test ròi nhe
    public ResponseEntity<CvSummaryDto> uploadNewCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("cvName") String cvName,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        CvSummaryDto newCv = cvService.uploadAndSaveCv(studentId, cvName, file);
        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
    }
//    @PostMapping("/upload-test")
//    public ResponseEntity<?> uploadNewCvTest(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("cvName") String cvName,
//            @RequestParam("studentId") String studentIdString
//    ) {
//        UUID studentId;
//
//        try {
//            studentId = UUID.fromString(studentIdString);
//        } catch (IllegalArgumentException e) {
//            return new ResponseEntity<>("Định dạng studentId (UUID) không hợp lệ.", HttpStatus.BAD_REQUEST);
//        }
//
//        CvSummaryDto newCv = cvService.uploadAndSaveCv(studentId, cvName, file);
//
//        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
//    }



    @GetMapping("/my-cvs") //TODO:test ròi nhe
    public ResponseEntity<List<CvSummaryDto>> getMyCVs(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        List<CvSummaryDto> cvs = cvService.findAllByStudentId(studentId);
        return ResponseEntity.ok(cvs);
    }


    @GetMapping("/{cvId}") //TODO:đã test, 1 bug studentId=null
    public ResponseEntity<CvDetailDto> getCvById(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        CvDetailDto cvDetail = cvService.findCvDetailById(cvId, studentId);
        return ResponseEntity.ok(cvDetail);
    }


    @PutMapping("/{cvId}") //TODO:đã test
    public ResponseEntity<CvSummaryDto> updateCvName(
            @PathVariable("cvId") Long cvId,
            @RequestBody UpdateCvNameRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        CvSummaryDto updatedCv = cvService.updateCvName(cvId, studentId, request.getNewCvName());
        return ResponseEntity.ok(updatedCv);
    }

    @DeleteMapping("/{cvId}") //TODO:đã test
    public ResponseEntity<Void> deleteCv(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        cvService.deleteCv(cvId, studentId);
        return ResponseEntity.noContent().build(); //204
        //TODO: xử lý trường hợp xóa CV mặc định
    }

    @PutMapping("/{cvId}/set-default") //TODO:đã test
    public ResponseEntity<String> setDefaultCv(
            @PathVariable("cvId") Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        cvService.setDefaultCv(cvId, studentId);
        return ResponseEntity.ok("CV với id " + cvId + " đã được đặt làm mặc định.");
    }

//API nội bộ
    @GetMapping("/{cvId}/structured-data")//TODO:chưa test
    public ResponseEntity<Object> getStructuredDataForMatching(@PathVariable Long cvId) {
        // Lưu ý: Cần có cơ chế bảo mật để đảm bảo chỉ các service nội bộ mới gọi được API này.
        Object structuredData = cvService.getStructuredDataForMatching(cvId);
        return ResponseEntity.ok(structuredData);
    }
}