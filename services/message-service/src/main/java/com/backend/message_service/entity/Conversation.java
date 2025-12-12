package com.backend.message_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "conversations",
        schema = "message_schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
        }
)
@Getter
@Setter
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user1Id luôn là UUID nhỏ hơn (normalize ở service)
    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "user1_last_read_message_id")
    private Long user1LastReadMessageId;

    @Column(name = "user2_last_read_message_id")
    private Long user2LastReadMessageId;

    @Column(name = "last_message_id")
    private Long lastMessageId;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
