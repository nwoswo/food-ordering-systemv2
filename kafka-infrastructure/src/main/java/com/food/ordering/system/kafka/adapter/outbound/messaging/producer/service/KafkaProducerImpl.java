package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducerImpl<K, V> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;
    private final KafkaMessageHelper kafkaMessageHelper;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate, KafkaMessageHelper kafkaMessageHelper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void send(String topicName, K key, V message, KafkaMessageHelper kafkaMessageHelper) {
        log.info("Sending message to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            CompletableFuture<SendResult<K, V>> completableFuture = kafkaTemplate.send(topicName, key, message);
            
            // Use CompletableFuture.whenComplete for modern async handling
            completableFuture.whenComplete((result, ex) -> {
                kafkaMessageHelper.handleKafkaCallback(result, ex, topicName, message, key.toString(), message.getClass().getSimpleName());
            });
        } catch (Exception e) {
            log.error("Error while sending message to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }

}
