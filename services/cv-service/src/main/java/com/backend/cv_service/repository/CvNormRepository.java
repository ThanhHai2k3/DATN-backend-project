package com.backend.cv_service.repository;

import com.backend.cv_service.entity.CvNorm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvNormRepository extends JpaRepository<CvNorm, Long> {
    // PK của CvNorm là cvId (Long), nên type thứ 2 là Long

    // Nếu sau này cần, bạn có thể thêm:
    // Optional<CvNorm> findByCvId(Long cvId);
}
