package com.smartqueue.queueservice.model;

import com.smartqueue.queueservice.enums.EntryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "queue_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userFullName;

    @Column(nullable = false)
    private int ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    private LocalDateTime joinedAt;
    private LocalDateTime calledAt;
    private LocalDateTime servedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = EntryStatus.WAITING;
        joinedAt = LocalDateTime.now();
    }
}