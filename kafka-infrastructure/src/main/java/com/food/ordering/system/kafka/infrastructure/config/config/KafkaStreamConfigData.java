package com.food.ordering.system.kafka.stream.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-stream-config")
public class KafkaStreamConfigData {
    private String bootstrapServers;
    private String orderEventsTopic;
    private String orderEventsGroupId;
    private Integer numOfPartitions;
    private Short replicationFactor;
    private String autoOffsetReset;
    private String contentType;
}
