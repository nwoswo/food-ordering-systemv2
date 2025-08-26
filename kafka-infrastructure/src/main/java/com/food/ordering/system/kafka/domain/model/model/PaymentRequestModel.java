package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestModel {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("sagaId")
    private String sagaId;
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("paymentOrderStatus")
    private PaymentOrderStatus paymentOrderStatus;
    
    public enum PaymentOrderStatus {
        PENDING, CANCELLED
    }
    
    public static PaymentRequestModel createPaymentRequest(
            String sagaId, String customerId, String orderId, BigDecimal price) {
        return PaymentRequestModel.builder()
                .id(java.util.UUID.randomUUID().toString())
                .sagaId(sagaId)
                .customerId(customerId)
                .orderId(orderId)
                .price(price)
                .createdAt(Instant.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }
}
