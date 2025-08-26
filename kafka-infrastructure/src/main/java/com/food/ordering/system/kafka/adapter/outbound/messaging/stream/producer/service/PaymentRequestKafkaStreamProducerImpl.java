package com.food.ordering.system.kafka.stream.producer.service;

import com.food.ordering.system.kafka.stream.producer.PaymentRequestKafkaStreamProducer;
import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class PaymentRequestKafkaStreamProducerImpl implements PaymentRequestKafkaStreamProducer {

    private final StreamBridge streamBridge;

    public PaymentRequestKafkaStreamProducerImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void send(String topicName, String key, PaymentRequestModel message) {
        log.info("Sending payment request to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            boolean sent = streamBridge.send(topicName, message);
            if (sent) {
                log.info("Payment request sent successfully to topic: {}", topicName);
            } else {
                log.error("Failed to send payment request to topic: {}", topicName);
                throw new RuntimeException("Failed to send payment request to Kafka");
            }
        } catch (Exception e) {
            log.error("Error while sending payment request to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending payment request to Kafka", e);
        }
    }
}
