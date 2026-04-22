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
public class QueueEventConsumer {

    private final ReportService reportService;
    private final ObjectMapper  objectMapper;

    @KafkaListener(
            topics = "queue.updated",
            groupId = "analytics-group"
    )
    public void onQueueUpdated(String message) {
        try {
            Map<String, Object> event =
                    objectMapper.readValue(message, Map.class);

            String centerId   = getString(event, "centerId");
            String centerName = getString(event, "centerName");
            int actualWait    = getInt(event, "actualWaitMinutes");
            int estimatedWait = getInt(event, "estimatedWaitMinutes");
            String queueId    = getString(event, "queueId");
            String userId     = getString(event, "userId");
            int ticketNumber  = getInt(event, "ticketNumber");

            if (!centerId.isEmpty()) {
                reportService.recordWaitTime(
                        UUID.fromString(centerId),
                        centerName,
                        UUID.fromString(queueId),
                        userId.isEmpty() ? null :
                                UUID.fromString(userId),
                        ticketNumber,
                        actualWait,
                        estimatedWait
                );
            }

            log.info("Processed QueueUpdated for center: {}",
                    centerName);

        } catch (Exception e) {
            log.error("Error processing QueueUpdated: {}",
                    e.getMessage());
        }
    }

    private String getString(
            Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private int getInt(
            Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0;
        if (val instanceof Integer i) return i;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); }
        catch (Exception e) { return 0; }
    }
}