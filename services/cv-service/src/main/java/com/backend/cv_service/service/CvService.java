package com.backend.cv_service.service;

import com.backend.cv_service.dto.CvDetailDto;
import com.backend.cv_service.dto.CvSummaryDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CvService {


//  Tải lên một file CV mới, lưu trữ và trả về thông tin tóm tắt.
    CvSummaryDto uploadExtractAndSaveCv(UUID studentId, String cvName, MultipartFile file);


//  Tìm tất cả CV (dạng tóm tắt) của một sinh viên.
    List<CvSummaryDto> findAllByStudentId(UUID studentId);


//  Tìm thông tin chi tiết của một CV, đồng thời xác thực quyền sở hữu.
    CvDetailDto findCvDetailById(Long cvId, UUID studentId);


//  Cập nhật tên của một CV, đồng thời xác thực quyền sở hữu.
    CvSummaryDto updateCvName(Long cvId, UUID studentId, String newCvName);


//  Xóa một CV, đồng thời xác thực quyền sở hữu.
    void deleteCv(Long cvId, UUID studentId);


//  Đặt một CV làm CV mặc định, đồng thời xác thực quyền sở hữu.
    void setDefaultCv(Long cvId, UUID studentId);


//  Lấy dữ liệu có cấu trúc của một CV để gửi cho matching-service.
//    Object getStructuredDataForMatching(Long cvId);


}