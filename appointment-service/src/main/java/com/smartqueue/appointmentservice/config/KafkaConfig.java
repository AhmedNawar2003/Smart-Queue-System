package com.smartqueue.appointmentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic appointmentCreatedTopic() {
        return TopicBuilder.name("appointment.created")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentCancelledTopic() {
        return TopicBuilder.name("appointment.cancelled")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentRescheduledTopic() {
        return TopicBuilder.name("appointment.rescheduled")
                .partitions(3).replicas(1).build();
    }
}