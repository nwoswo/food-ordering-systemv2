package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestModel;

public interface RestaurantApprovalRequestKafkaStreamProducer {
    void send(String topicName, String key, RestaurantApprovalRequestModel message);
}
