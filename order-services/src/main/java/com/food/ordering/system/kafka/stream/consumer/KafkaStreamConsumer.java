package com.food.ordering.system.kafka.stream.consumer;

import java.util.List;

public interface KafkaStreamConsumer<T> {
    void receive(List<T> messages);
}
