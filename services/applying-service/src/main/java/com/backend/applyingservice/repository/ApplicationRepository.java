package com.backend.applyingservice.repository;

import com.backend.applyingservice.entity.Application;
import com.backend.applyingservice.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByStudentIdAndJobPostId(UUID studentId, UUID jobPostId);

    //Sinh vien xem ho so cua minh
    Optional<Application> findByIdAndStudentId(UUID id, UUID studentId);

    //Lay ho so ung tuyen cua SV
    List<Application> findByStudentIdOrderByAppliedAtDesc(UUID studentId);

    Optional<Application> findByIdAndEmployerId(UUID id, UUID employerId);

    //NTD lay danh sach ung vien
    Page<Application> findByJobPostIdAndEmployerId(UUID jobPostId, UUID employerId, Pageable pageable);
    //Loc trang thai
    List<Application> findByJobPostIdAndStatus(UUID jobPostId, ApplicationStatus status);

    @Modifying
    @Query("""
        update Application a
           set a.status = :status,
               a.viewedAt = :viewedAt
         where a.id = :id
           and a.employerId = :employerId
           and a.viewedAt is null
    """)
    int markAsViewedIfNotViewed(
            @Param("id") UUID id,
            @Param("employerId") UUID employerId,
            @Param("status") ApplicationStatus status,
            @Param("viewedAt") Instant viewedAt
    );

    // Đếm tổng số đơn ứng tuyển cho danh sách các bài Job
    long countByJobPostIdIn(List<UUID> jobPostIds);

    // Đếm số đơn theo danh sách Job VÀ trạng thái cụ thể
    long countByJobPostIdInAndStatus(List<UUID> jobPostIds, ApplicationStatus status);
}
