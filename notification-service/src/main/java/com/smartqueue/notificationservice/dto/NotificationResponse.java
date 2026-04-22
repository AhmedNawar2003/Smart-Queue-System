package com.smartqueue.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartqueue.notificationservice.enums.NotificationStatus;
import com.smartqueue.notificationservice.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(
        @JsonProperty("id")          UUID id,
        @JsonProperty("title")       String title,
        @JsonProperty("message")     String message,
        @JsonProperty("type")        NotificationType type,
        @JsonProperty("status")      NotificationStatus status,
        @JsonProperty("referenceId") String referenceId,
        @JsonProperty("sentAt")      LocalDateTime sentAt,
        @JsonProperty("readAt")      LocalDateTime readAt,
        @JsonProperty("createdAt")   LocalDateTime createdAt
) {}