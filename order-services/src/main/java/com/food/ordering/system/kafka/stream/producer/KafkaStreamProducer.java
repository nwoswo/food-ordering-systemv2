package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.OrderEvent;

public interface KafkaStreamProducer {
    void send(String topicName, String key, OrderEvent message);
}
