package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;

public interface PaymentResponseKafkaStreamProducer {
    void send(String topicName, String key, PaymentResponseModel message);
}
