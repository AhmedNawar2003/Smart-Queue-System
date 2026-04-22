package com.smartqueue.queueservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String TOPIC_QUEUE_UPDATED  = "queue.updated";
    public static final String TOPIC_TURN_SOON      = "queue.turn-soon";
    public static final String TOPIC_TICKET_ISSUED  = "queue.ticket-issued";

    public void publishQueueUpdated(UUID queueId,
                                    int currentPosition,
                                    UUID centerId,
                                    String centerName) {
        var event = new QueueUpdatedEvent(
                queueId, centerId, centerName,
                currentPosition, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_QUEUE_UPDATED,
                queueId.toString(), event);
    }

    // لما يبقى في 2 أشخاص قبلك
    public void publishTurnSoon(UUID userId, String userEmail,
                                UUID queueId, int ticketNumber,
                                int etaMinutes) {
        var event = new TurnSoonEvent(
                userId, userEmail, queueId,
                ticketNumber, etaMinutes, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_TURN_SOON,
                userId.toString(), event);
        log.info("Published TurnSoon: user={} ticket={}",
                userEmail, ticketNumber);
    }

    // لما حد ياخد ticket
    public void publishTicketIssued(UUID userId, String userEmail,
                                    UUID queueId, int ticketNumber,
                                    int etaMinutes) {
        var event = new TicketIssuedEvent(
                userId, userEmail, queueId,
                ticketNumber, etaMinutes, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_TICKET_ISSUED,
                userId.toString(), event);
        log.info("Published TicketIssued: user={} ticket={}",
                userEmail, ticketNumber);
    }

    // ── Event Records ────────────────────────────────────────

    public record QueueUpdatedEvent(
            UUID queueId,
            UUID centerId,
            String centerName,
            int currentPosition,
            LocalDateTime timestamp) {}

    public record TurnSoonEvent(
            UUID userId,
            String userEmail,
            UUID queueId,
            int ticketNumber,
            int etaMinutes,
            LocalDateTime timestamp) {}

    public record TicketIssuedEvent(
            UUID userId,
            String userEmail,
            UUID queueId,
            int ticketNumber,
            int etaMinutes,
            LocalDateTime timestamp) {}
}