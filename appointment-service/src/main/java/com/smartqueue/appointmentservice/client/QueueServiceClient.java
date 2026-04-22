package com.smartqueue.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "queue-service", path = "/api/v1/queues")
public interface QueueServiceClient {

    @GetMapping("/{queueId}/position/{ticketNumber}")
    QueuePositionResponse getPosition(
            @PathVariable UUID queueId,
            @PathVariable int ticketNumber);

    record QueuePositionResponse(
            UUID queueId,
            int ticketNumber,
            int currentPosition,
            int waitingAhead,
            int estimatedWaitMinutes,
            String status
    ) {}
}