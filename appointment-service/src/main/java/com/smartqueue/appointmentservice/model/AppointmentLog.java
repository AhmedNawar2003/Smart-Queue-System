package com.smartqueue.appointmentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppointmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private String action;      // CREATED, RESCHEDULED, CANCELLED

    @Column(nullable = false)
    private String actor;       // email of who did the action

    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}