package com.smartqueue.queueservice.model;

import com.smartqueue.queueservice.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "queues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID centerId;

    @Column(nullable = false)
    private String centerName;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;

    @Column(nullable = false)
    private int currentPosition;

    @Column(nullable = false)
    private int totalIssued;

    @Column(nullable = false)
    private int avgServiceMinutes;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = QueueStatus.OPEN;
        if (avgServiceMinutes == 0) avgServiceMinutes = 5;
        openedAt = LocalDateTime.now();
    }
}