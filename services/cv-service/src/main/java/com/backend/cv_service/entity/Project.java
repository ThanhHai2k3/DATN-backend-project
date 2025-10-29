package com.backend.cv_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @Entity
    @Table(name = "projects", schema = "cv_schema")
    public class Project {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "project_name", nullable = false)
        private String projectName;

        @Column(name = "project_url")
        private String projectUrl;

        // Mối quan hệ Nhiều-Một với CV
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cv_id", nullable = false)
        private CV cv;
    }