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
@RequestMapping("/api/v1/cvs")
public class CvController {

    @Autowired
    private CvService cvService;

    @Autowired
    private JwtUtil jwtUtil; // Lớp tiện ích để giải mã token

    /**
     * Endpoint để upload một CV mới.
     */
//    @PostMapping("/upload")
//    public ResponseEntity<CvSummaryDto> uploadNewCv(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("cvName") String cvName,
//            @RequestHeader("Authorization") String authorizationHeader
//    ) {
//        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
//        CvSummaryDto newCv = cvService.uploadAndSaveCv(studentId, cvName, file);
//        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
//    }
    @PostMapping("/upload-test")
    public ResponseEntity<?> uploadNewCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("cvName") String cvName,
            @RequestParam("studentId") String studentIdString
    ) {
        UUID studentId;

        try {
            studentId = UUID.fromString(studentIdString);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Định dạng studentId (UUID) không hợp lệ.", HttpStatus.BAD_REQUEST);
        }

        // 3. Vẫn gọi CvService như bình thường
        CvSummaryDto newCv = cvService.uploadAndSaveCv(studentId, cvName, file);

        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
    }

    // --- Lấy thông tin CV ---

    /**
     * Lấy danh sách tóm tắt tất cả CV của người dùng đang đăng nhập.
     */
    @GetMapping("/my-cvs")
    public ResponseEntity<List<CvSummaryDto>> getMyCVs(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        List<CvSummaryDto> cvs = cvService.findAllByStudentId(studentId);
        return ResponseEntity.ok(cvs);
    }

    /**
     * Lấy thông tin chi tiết của một CV cụ thể.
     * Đảm bảo người dùng chỉ có thể xem CV của chính họ.
     */
    @GetMapping("/{cvId}")
    public ResponseEntity<CvDetailDto> getCvById(
            @PathVariable Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        CvDetailDto cvDetail = cvService.findCvDetailById(cvId, studentId);
        return ResponseEntity.ok(cvDetail);
    }

    // --- Chỉnh sửa và Xóa CV ---

    /**
     * Cập nhật tên của một CV.
     */
    @PutMapping("/{cvId}")
    public ResponseEntity<CvSummaryDto> updateCvName(
            @PathVariable Long cvId,
            @RequestBody UpdateCvNameRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        CvSummaryDto updatedCv = cvService.updateCvName(cvId, studentId, request.getNewCvName());
        return ResponseEntity.ok(updatedCv);
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<Void> deleteCv(
            @PathVariable Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        cvService.deleteCv(cvId, studentId);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content là chuẩn cho API DELETE
    }

    // --- Chức năng đặc biệt ---

    /**
     * Đặt một CV làm CV mặc định.
     */
    @PutMapping("/{cvId}/set-default")
    public ResponseEntity<String> setDefaultCv(
            @PathVariable Long cvId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID studentId = jwtUtil.extractUserIdFromToken(authorizationHeader);
        cvService.setDefaultCv(cvId, studentId);
        return ResponseEntity.ok("CV with id " + cvId + " has been set as default.");
    }

    /**
     * API nội bộ: Lấy dữ liệu có cấu trúc của CV để phục vụ cho matching-service.
     * API này có thể có cơ chế bảo mật riêng (ví dụ: chỉ cho phép gọi từ gateway).
     */
    @GetMapping("/{cvId}/structured-data")
    public ResponseEntity<Object> getStructuredDataForMatching(@PathVariable Long cvId) {
        // Lưu ý: Cần có cơ chế bảo mật để đảm bảo chỉ các service nội bộ mới gọi được API này.
        Object structuredData = cvService.getStructuredDataForMatching(cvId);
        return ResponseEntity.ok(structuredData);
    }
}