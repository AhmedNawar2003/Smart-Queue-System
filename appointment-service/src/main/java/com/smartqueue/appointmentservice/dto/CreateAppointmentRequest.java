package com.smartqueue.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateAppointmentRequest {

    @NotNull(message = "Center ID is required")
    private UUID centerId;

    @NotBlank(message = "Center name is required")
    private String centerName;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime scheduledAt;

    private String notes;

    // لو الـ user عايز يربط الـ appointment بـ queue entry
    private UUID queueId;
    private Integer ticketNumber;
}