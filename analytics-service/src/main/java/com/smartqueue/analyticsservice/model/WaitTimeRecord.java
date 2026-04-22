package com.smartqueue.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wait_time_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WaitTimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID centerId;

    @Column(nullable = false)
    private String centerName;

    @Column(nullable = false)
    private UUID queueId;

    private UUID userId;

    private int ticketNumber;

    // وقت الانتظار الفعلي بالدقائق
    private int actualWaitMinutes;

    // وقت الانتظار المتوقع اللي اتحسبله
    private int estimatedWaitMinutes;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordDate == null) recordDate = LocalDate.now();
    }
}