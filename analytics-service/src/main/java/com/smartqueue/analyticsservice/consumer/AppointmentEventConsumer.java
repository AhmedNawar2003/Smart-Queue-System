package com.smartqueue.analyticsservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartqueue.analyticsservice.service.ReportService;
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

    private final ReportService reportService;
    private final ObjectMapper  objectMapper;

    @KafkaListener(
            topics = "appointment.created",
            groupId = "analytics-group"
    )
    public void onAppointmentCreated(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String centerId   = getString(event, "centerId");
            String centerName = getString(event, "centerName");

            if (!centerId.isEmpty()) {
                reportService.incrementAppointments(
                        UUID.fromString(centerId), centerName);
            }

            log.info("Processed AppointmentCreated analytics");

        } catch (Exception e) {
            log.error(
                    "Error processing AppointmentCreated: {}",
                    e.getMessage());
        }
    }

    @KafkaListener(
            topics = "appointment.cancelled",
            groupId = "analytics-group"
    )
    public void onAppointmentCancelled(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String centerId   = getString(event, "centerId");
            String centerName = getString(event, "centerName");

            if (!centerId.isEmpty()) {
                reportService.incrementCancelled(
                        UUID.fromString(centerId), centerName);
            }

            log.info("Processed AppointmentCancelled analytics");

        } catch (Exception e) {
            log.error(
                    "Error processing AppointmentCancelled: {}",
                    e.getMessage());
        }
    }

    private String getString(
            Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }
}