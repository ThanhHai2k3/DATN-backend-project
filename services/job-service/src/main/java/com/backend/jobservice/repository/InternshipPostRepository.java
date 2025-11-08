package com.backend.jobservice.repository;

import com.backend.jobservice.entity.InternshipPost;
import com.backend.jobservice.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternshipPostRepository extends JpaRepository<InternshipPost, UUID> {
    List<InternshipPost> findByStatus(PostStatus status);
    List<InternshipPost> findByCompanyId(UUID companyId);
    Optional<InternshipPost> findByIdAndPostedBy(UUID id, UUID postedBy);
    List<InternshipPost> findByTitleContainingIgnoreCaseAndStatus(String keyword, PostStatus status);
}
