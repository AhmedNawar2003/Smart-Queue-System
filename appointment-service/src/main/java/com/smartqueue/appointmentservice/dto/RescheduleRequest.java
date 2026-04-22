package com.smartqueue.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleRequest {

    @NotNull(message = "New scheduled time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime newScheduledAt;

    private String reason;
}