package com.food.ordering.system.kafka.stream.consumer;

import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;

import java.util.List;

public interface PaymentRequestKafkaStreamConsumer {
    void receive(List<PaymentRequestModel> messages);
}
