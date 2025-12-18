package com.backend.jobservice.repository.specification;

import com.backend.jobservice.entity.InternshipPost;
import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.enums.WorkMode;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public class InternshipPostSpecification {

    public static Specification<InternshipPost> hasStatus(PostStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<InternshipPost> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("position")), pattern)
            );
        };
    }

    public static Specification<InternshipPost> hasWorkMode(String workModeStr) {
        return (root, query, cb) -> {
            if (workModeStr == null || workModeStr.isBlank() || workModeStr.equalsIgnoreCase("ALL")) return null;
            try {
                WorkMode mode = WorkMode.valueOf(workModeStr.toUpperCase());
                return cb.equal(root.get("workMode"), mode);
            } catch (Exception e) {
                return null;
            }
        };
    }

    public static Specification<InternshipPost> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) return null;
            return cb.equal(root.get("location"), location);
        };
    }

    public static Specification<InternshipPost> hasSkill(UUID skillId) {
        return (root, query, cb) -> {
            if (skillId == null) return null;
            query.distinct(true);
            return cb.equal(root.join("jobSkills").get("skillId"), skillId);
        };
    }
}