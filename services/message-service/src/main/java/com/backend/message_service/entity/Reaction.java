package com.backend.message_service.entity;

import com.backend.message_service.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

//    @Column(name = "reaction_type", nullable = false, length = 50)
//    private String reactionType;  // sớm sẽ bổ sung enum reaction type

    @Enumerated(EnumType.STRING) // <-- Rất quan trọng!
    @Column(name = "reaction_type", nullable = false, length = 50)
    private ReactionType reactionType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}