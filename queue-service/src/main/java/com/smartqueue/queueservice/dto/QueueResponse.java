package com.smartqueue.queueservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartqueue.queueservice.enums.QueueStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record QueueResponse(
        @JsonProperty("id")                UUID id,
        @JsonProperty("centerId")          UUID centerId,
        @JsonProperty("centerName")        String centerName,
        @JsonProperty("date")              LocalDate date,
        @JsonProperty("status")            QueueStatus status,
        @JsonProperty("currentPosition")   int currentPosition,
        @JsonProperty("totalIssued")       int totalIssued,
        @JsonProperty("avgServiceMinutes") int avgServiceMinutes,
        @JsonProperty("waitingCount")      int waitingCount,
        @JsonProperty("openedAt")          LocalDateTime openedAt
) {}