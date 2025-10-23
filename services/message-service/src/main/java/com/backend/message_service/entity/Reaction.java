package com.backend.message_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(
        name = "reactions",
        schema = "message_schema",
        uniqueConstraints = {
                // Một user chỉ được thả 1 loại reaction trên 1 message
                @UniqueConstraint(columnNames = {"message_id", "user_id", "reaction_type"})
        }
)
@Getter
@Setter
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reaction_type", nullable = false, length = 50)
    private String reactionType;  // sớm sẽ bổ sung enum reaction type

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}