package com.smartqueue.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.notificationservice.enums.NotificationType;
import com.smartqueue.notificationservice.service.EmailService;
import com.smartqueue.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventConsumer {

    private final NotificationService notificationService;
    private final EmailService        emailService;
    private final ObjectMapper        objectMapper;

    @KafkaListener(
            topics = "queue.ticket-issued",
            groupId = "notification-group"
    )
    public void onTicketIssued(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String userEmail = getString(event, "userEmail");
            String queueId   = getString(event, "queueId");
            int ticketNumber = getInt(event, "ticketNumber");
            int etaMinutes   = getInt(event, "etaMinutes");

            UUID userId = UUID.nameUUIDFromBytes(
                    userEmail.getBytes());

            notificationService.createAndSend(
                    userId, userEmail,
                    "🎫 تذكرتك رقم " + ticketNumber,
                    "انضممت للطابور — وقت الانتظار المتوقع " +
                            etaMinutes + " دقيقة",
                    NotificationType.TICKET_ISSUED,
                    queueId
            );

            emailService.sendTicketIssuedNotification(
                    userEmail, userEmail,
                    ticketNumber, etaMinutes);

            log.info("Processed TicketIssued for: {} ticket: {}",
                    userEmail, ticketNumber);

        } catch (Exception e) {
            log.error("Error processing TicketIssued: {}",
                    e.getMessage());
        }
    }

    @KafkaListener(
            topics = "queue.turn-soon",
            groupId = "notification-group"
    )
    public void onTurnSoon(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String userEmail = getString(event, "userEmail");
            String queueId   = getString(event, "queueId");
            int ticketNumber = getInt(event, "ticketNumber");
            int etaMinutes   = getInt(event, "etaMinutes");

            UUID userId = UUID.nameUUIDFromBytes(
                    userEmail.getBytes());

            notificationService.createAndSend(
                    userId, userEmail,
                    "🔔 دورك قريب — رقم " + ticketNumber,
                    "متبقي تقريباً " + etaMinutes + " دقيقة",
                    NotificationType.TURN_SOON,
                    queueId
            );

            emailService.sendTurnSoonNotification(
                    userEmail, userEmail,
                    ticketNumber, etaMinutes);

            log.info("Processed TurnSoon for: {} ticket: {}",
                    userEmail, ticketNumber);

        } catch (Exception e) {
            log.error("Error processing TurnSoon: {}",
                    e.getMessage());
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private int getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0;
        if (val instanceof Integer i) return i;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); }
        catch (Exception e) { return 0; }
    }
}