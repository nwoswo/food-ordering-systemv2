package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private double totalAmount;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public static OrderEvent createOrderEvent(String orderId, String customerId, String status, double totalAmount) {
        return OrderEvent.builder()
                .orderId(orderId)
                .customerId(customerId)
                .status(status)
                .totalAmount(totalAmount)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
