package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;

public interface PaymentRequestKafkaStreamProducer {
    void send(String topicName, String key, PaymentRequestModel message);
}
