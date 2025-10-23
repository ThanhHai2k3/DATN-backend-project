package com.backend.message_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(
        name = "conversations",
        schema = "message_schema",
        //  đảm bảo không có cặp (user1, user2) nào bị trùng
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "user1_last_read_message_id")
    private Long user1LastReadMessageId;

    @Column(name = "user2_last_read_message_id")
    private Long user2LastReadMessageId;

    @Column(name = "last_message_id")
    private Long lastMessageId;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // note: nhớ nhắc duke bổ sung code kiểm tra userid1<userid2 trong service nhé
}