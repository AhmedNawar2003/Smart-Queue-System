package com.smartqueue.appointmentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String TOPIC_CREATED     =
            "appointment.created";
    public static final String TOPIC_RESCHEDULED =
            "appointment.rescheduled";
    public static final String TOPIC_CANCELLED   =
            "appointment.cancelled";
    public static final String TOPIC_COMPLETED   =
            "appointment.completed";

    public void publishCreated(UUID appointmentId,
                               UUID userId,
                               String userEmail,
                               String centerName,
                               String serviceType,
                               LocalDateTime scheduledAt) {
        var event = new AppointmentCreatedEvent(
                appointmentId, userId, userEmail,
                centerName, serviceType,
                scheduledAt, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_CREATED,
                appointmentId.toString(), event);
        log.info("Published AppointmentCreated: {}",
                appointmentId);
    }

    public void publishCancelled(UUID appointmentId,
                                 UUID userId,
                                 String userEmail,
                                 String reason) {
        var event = new AppointmentCancelledEvent(
                appointmentId, userId, userEmail,
                reason, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_CANCELLED,
                appointmentId.toString(), event);
        log.info("Published AppointmentCancelled: {}",
                appointmentId);
    }

    public void publishRescheduled(UUID appointmentId,
                                   UUID userId,
                                   String userEmail,
                                   LocalDateTime newScheduledAt) {
        var event = new AppointmentRescheduledEvent(
                appointmentId, userId, userEmail,
                newScheduledAt, LocalDateTime.now());
        kafkaTemplate.send(TOPIC_RESCHEDULED,
                appointmentId.toString(), event);
        log.info("Published AppointmentRescheduled: {}",
                appointmentId);
    }

    // ── Event Records ────────────────────────────────────────

    public record AppointmentCreatedEvent(
            UUID appointmentId,
            UUID userId,
            String userEmail,
            String centerName,
            String serviceType,
            LocalDateTime scheduledAt,
            LocalDateTime timestamp) {}

    public record AppointmentCancelledEvent(
            UUID appointmentId,
            UUID userId,
            String userEmail,
            String reason,
            LocalDateTime timestamp) {}

    public record AppointmentRescheduledEvent(
            UUID appointmentId,
            UUID userId,
            String userEmail,
            LocalDateTime newScheduledAt,
            LocalDateTime timestamp) {}
}