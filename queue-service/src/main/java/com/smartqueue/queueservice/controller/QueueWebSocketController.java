package com.smartqueue.queueservice.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class QueueWebSocketController {

    // Client يعمل subscribe على: /topic/queue/{queueId}
    // Server يعمل push عبر SimpMessagingTemplate في QueueService

    @MessageMapping("/queue/{queueId}/subscribe")
    @SendTo("/topic/queue/{queueId}")
    public String subscribe(@DestinationVariable UUID queueId) {
        return "Subscribed to queue: " + queueId;
    }
}