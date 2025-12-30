package com.backend.authservice.repository;

import com.backend.authservice.entity.UserAccount;
import com.backend.authservice.enums.AccountStatus;
import com.backend.authservice.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query(
            value = """
    SELECT *
    FROM auth_schema.user_accounts ua
    WHERE (:q IS NULL OR ua.email ILIKE CONCAT('%%', CAST(:q AS text), '%%'))
      AND (:role IS NULL OR ua.role = CAST(:role AS text))
      AND (:status IS NULL OR ua.status = CAST(:status AS text))
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM auth_schema.user_accounts ua
    WHERE (:q IS NULL OR ua.email ILIKE CONCAT('%%', CAST(:q AS text), '%%'))
      AND (:role IS NULL OR ua.role = CAST(:role AS text))
      AND (:status IS NULL OR ua.status = CAST(:status AS text))
    """,
            nativeQuery = true
    )
    Page<UserAccount> search(
            @Param("q") String q,
            @Param("role") String role,
            @Param("status") String status,
            Pageable pageable
    );

}
