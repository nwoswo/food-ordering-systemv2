package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantApprovalResponseModel {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("sagaId")
    private UUID sagaId;
    
    @JsonProperty("restaurantId")
    private UUID restaurantId;
    
    @JsonProperty("orderId")
    private UUID orderId;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("orderApprovalStatus")
    private OrderApprovalStatus orderApprovalStatus;
    
    @JsonProperty("failureMessages")
    private List<String> failureMessages;
    
    public enum OrderApprovalStatus {
        APPROVED, REJECTED
    }
    
    public static RestaurantApprovalResponseModel createRestaurantApprovalResponse(
            UUID sagaId, UUID restaurantId, UUID orderId, 
            OrderApprovalStatus orderApprovalStatus, List<String> failureMessages) {
        return RestaurantApprovalResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .restaurantId(restaurantId)
                .orderId(orderId)
                .createdAt(Instant.now())
                .orderApprovalStatus(orderApprovalStatus)
                .failureMessages(failureMessages)
                .build();
    }
}
