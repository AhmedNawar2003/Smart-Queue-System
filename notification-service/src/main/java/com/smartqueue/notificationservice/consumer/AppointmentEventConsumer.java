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
public class AppointmentEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "appointment.created",
            groupId = "notification-group"
    )
    public void onAppointmentCreated(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String userEmail    = getString(event, "userEmail");
            String centerName   = getString(event, "centerName");
            String serviceType  = getString(event, "serviceType");
            String scheduledAt  = getString(event, "scheduledAt");
            String appointmentId = getString(event, "appointmentId");

            UUID userId = UUID.nameUUIDFromBytes(
                    userEmail.getBytes());

            notificationService.createAndSend(
                    userId, userEmail,
                    "✅ تم تأكيد حجزك في " + centerName,
                    "تم حجز موعدك لـ " + serviceType +
                            " بتاريخ " + scheduledAt,
                    NotificationType.APPOINTMENT_CREATED,
                    appointmentId
            );

            emailService.sendAppointmentConfirmation(
                    userEmail, userEmail,
                    centerName, serviceType, scheduledAt);

            log.info("Processed AppointmentCreated for: {}",
                    userEmail);

        } catch (Exception e) {
            log.error("Error processing AppointmentCreated: {}",
                    e.getMessage());
        }
    }

    @KafkaListener(
            topics = "appointment.cancelled",
            groupId = "notification-group"
    )
    public void onAppointmentCancelled(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String userEmail     = getString(event, "userEmail");
            String appointmentId = getString(event, "appointmentId");

            UUID userId = UUID.nameUUIDFromBytes(
                    userEmail.getBytes());

            notificationService.createAndSend(
                    userId, userEmail,
                    "❌ تم إلغاء الحجز",
                    "تم إلغاء حجزك بنجاح",
                    NotificationType.APPOINTMENT_CANCELLED,
                    appointmentId
            );

            log.info("Processed AppointmentCancelled for: {}",
                    userEmail);

        } catch (Exception e) {
            log.error("Error processing AppointmentCancelled: {}",
                    e.getMessage());
        }
    }

    @KafkaListener(
            topics = "appointment.rescheduled",
            groupId = "notification-group"
    )
    public void onAppointmentRescheduled(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String userEmail      = getString(event, "userEmail");
            String newScheduledAt = getString(event, "newScheduledAt");
            String appointmentId  = getString(event, "appointmentId");

            UUID userId = UUID.nameUUIDFromBytes(
                    userEmail.getBytes());

            notificationService.createAndSend(
                    userId, userEmail,
                    "🔄 تم تغيير موعدك",
                    "تم تحديث موعدك إلى " + newScheduledAt,
                    NotificationType.APPOINTMENT_RESCHEDULED,
                    appointmentId
            );

            log.info("Processed AppointmentRescheduled for: {}",
                    userEmail);

        } catch (Exception e) {
            log.error("Error processing AppointmentRescheduled: {}",
                    e.getMessage());
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }
}