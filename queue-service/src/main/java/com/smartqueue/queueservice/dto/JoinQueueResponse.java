package com.smartqueue.queueservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
public record JoinQueueResponse(
        @JsonProperty("entryId")              UUID entryId,
        @JsonProperty("queueId")              UUID queueId,
        @JsonProperty("ticketNumber")         int ticketNumber,
        @JsonProperty("waitingAhead")         int waitingAhead,
        @JsonProperty("estimatedWaitMinutes") int estimatedWaitMinutes,
        @JsonProperty("qrCodeData")           String qrCodeData
) {}