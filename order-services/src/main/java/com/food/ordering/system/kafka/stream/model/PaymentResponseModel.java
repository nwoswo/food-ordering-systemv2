package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseModel {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("sagaId")
    private UUID sagaId;
    
    @JsonProperty("paymentId")
    private UUID paymentId;
    
    @JsonProperty("customerId")
    private UUID customerId;
    
    @JsonProperty("orderId")
    private UUID orderId;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("paymentStatus")
    private PaymentStatus paymentStatus;
    
    @JsonProperty("failureMessages")
    private List<String> failureMessages;
    
    public enum PaymentStatus {
        COMPLETED, CANCELLED, FAILED
    }
    
    public static PaymentResponseModel createPaymentResponse(
            UUID sagaId, UUID paymentId, UUID customerId, UUID orderId, 
            BigDecimal price, PaymentStatus paymentStatus, List<String> failureMessages) {
        return PaymentResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .paymentId(paymentId)
                .customerId(customerId)
                .orderId(orderId)
                .price(price)
                .createdAt(Instant.now())
                .paymentStatus(paymentStatus)
                .failureMessages(failureMessages)
                .build();
    }
}
