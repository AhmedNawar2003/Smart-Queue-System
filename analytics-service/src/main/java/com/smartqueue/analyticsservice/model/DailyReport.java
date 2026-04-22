package com.smartqueue.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_reports",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"center_id", "report_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @Column(nullable = false)
    private String centerName;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    private int totalServed;
    private int totalCancelled;
    private int totalNoShows;
    private int totalAppointments;

    // متوسط وقت الانتظار الفعلي
    private double avgActualWaitMinutes;

    // متوسط وقت الانتظار المتوقع
    private double avgEstimatedWaitMinutes;

    // دقة التوقع — نسبة مئوية
    private double predictionAccuracy;

    private int peakHour;   // الساعة الأكثر ازدحاماً

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}