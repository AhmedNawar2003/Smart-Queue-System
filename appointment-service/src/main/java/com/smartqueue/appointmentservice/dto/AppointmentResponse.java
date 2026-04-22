package com.smartqueue.appointmentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartqueue.appointmentservice.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AppointmentResponse(
        @JsonProperty("id")            UUID id,
        @JsonProperty("userId")        UUID userId,
        @JsonProperty("userEmail")     String userEmail,
        @JsonProperty("userFullName")  String userFullName,
        @JsonProperty("centerId")      UUID centerId,
        @JsonProperty("centerName")    String centerName,
        @JsonProperty("serviceType")   String serviceType,
        @JsonProperty("status")        AppointmentStatus status,
        @JsonProperty("scheduledAt")   LocalDateTime scheduledAt,
        @JsonProperty("completedAt")   LocalDateTime completedAt,
        @JsonProperty("queueId")       UUID queueId,
        @JsonProperty("ticketNumber")  Integer ticketNumber,
        @JsonProperty("notes")         String notes,
        @JsonProperty("createdAt")     LocalDateTime createdAt,
        @JsonProperty("updatedAt")     LocalDateTime updatedAt
) {}