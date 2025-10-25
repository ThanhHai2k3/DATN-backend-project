package com.backend.message_service.entity;

import com.backend.message_service.enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "messages", schema = "message_schema")
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    private MessageType messageType;

//    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private Instant sentAt;
}