package com.backend.profileservice.repository;

import com.backend.profileservice.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, UUID> {

    List<SocialLink> findByStudentId(UUID studentId);

    Optional<SocialLink> findByIdAndStudentUserId(UUID linkId, UUID userId);

    void deleteByStudentId(UUID studentId);
}
