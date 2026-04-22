package com.smartqueue.queueservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartqueue.queueservice.enums.EntryStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record QueuePositionResponse(
        @JsonProperty("queueId")              UUID queueId,
        @JsonProperty("ticketNumber")         int ticketNumber,
        @JsonProperty("currentPosition")      int currentPosition,
        @JsonProperty("waitingAhead")         int waitingAhead,
        @JsonProperty("estimatedWaitMinutes") int estimatedWaitMinutes,
        @JsonProperty("status")               EntryStatus status
) {}