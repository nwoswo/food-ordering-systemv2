package com.food.ordering.system.kafka.stream.producer.service;

import com.food.ordering.system.kafka.stream.producer.KafkaStreamProducer;
import com.food.ordering.system.kafka.stream.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class KafkaStreamProducerImpl implements KafkaStreamProducer {

    private final StreamBridge streamBridge;

    public KafkaStreamProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void send(String topicName, String key, OrderEvent message) {
        log.info("Sending message to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            boolean sent = streamBridge.send(topicName, message);
            if (sent) {
                log.info("Message sent successfully to topic: {}", topicName);
            } else {
                log.error("Failed to send message to topic: {}", topicName);
                throw new RuntimeException("Failed to send message to Kafka");
            }
        } catch (Exception e) {
            log.error("Error while sending message to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}
