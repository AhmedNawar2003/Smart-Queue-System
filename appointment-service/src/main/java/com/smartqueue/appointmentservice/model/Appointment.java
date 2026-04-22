package com.smartqueue.appointmentservice.model;

import com.smartqueue.appointmentservice.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userFullName;

    @Column(nullable = false)
    private UUID centerId;

    @Column(nullable = false)
    private String centerName;

    private UUID queueId;

    private Integer ticketNumber;

    @Column(nullable = false)
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime completedAt;
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = AppointmentStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}