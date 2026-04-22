package com.smartqueue.queueservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic queueUpdatedTopic() {
        return TopicBuilder.name("queue.updated")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic turnSoonTopic() {
        return TopicBuilder.name("queue.turn-soon")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic ticketIssuedTopic() {
        return TopicBuilder.name("queue.ticket-issued")
                .partitions(3).replicas(1).build();
    }
}