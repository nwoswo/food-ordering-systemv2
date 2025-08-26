package com.food.ordering.system.kafka.producer;

public interface KafkaProducer<K, V> {
    void send(String topicName, K key, V message, KafkaMessageHelper kafkaMessageHelper);
}
