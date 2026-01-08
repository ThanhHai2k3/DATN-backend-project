package com.backend.jobservice.repository;

import com.backend.jobservice.entity.InternshipPost;
import com.backend.jobservice.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternshipPostRepository extends JpaRepository<InternshipPost, UUID>, JpaSpecificationExecutor<InternshipPost> {

    List<InternshipPost> findByStatusOrderByCreatedAtDesc(PostStatus status);
    List<InternshipPost> findAllByOrderByCreatedAtDesc();
    List<InternshipPost> findByStatus(PostStatus status);
    List<InternshipPost> findByCompanyId(UUID companyId);
    Optional<InternshipPost> findByIdAndPostedBy(UUID id, UUID postedBy);
    Optional<InternshipPost> findByIdAndStatus(UUID id, PostStatus status);
    List<InternshipPost> findByTitleContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(String keyword, PostStatus status);


    Page<InternshipPost> findByPostedBy(UUID postedBy, Pageable pageable);

    @Modifying
    @Query("""
        update InternshipPost p
        set p.status = com.backend.jobservice.enums.PostStatus.EXPIRED,
            p.updatedAt = :now
        where p.status = com.backend.jobservice.enums.PostStatus.ACTIVE
          and p.expiredAt is not null
          and p.expiredAt <= :now
    """)
    int markExpiredPosts(@Param("now") Instant now);
}
