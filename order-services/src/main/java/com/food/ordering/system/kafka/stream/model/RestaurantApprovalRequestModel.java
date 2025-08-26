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
public class RestaurantApprovalRequestModel {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("sagaId")
    private UUID sagaId;
    
    @JsonProperty("restaurantId")
    private UUID restaurantId;
    
    @JsonProperty("orderId")
    private UUID orderId;
    
    @JsonProperty("restaurantOrderStatus")
    private RestaurantOrderStatus restaurantOrderStatus;
    
    @JsonProperty("products")
    private List<Product> products;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    public enum RestaurantOrderStatus {
        PAID
    }
    
    public static RestaurantApprovalRequestModel createRestaurantApprovalRequest(
            UUID sagaId, UUID restaurantId, UUID orderId, 
            RestaurantOrderStatus restaurantOrderStatus, List<Product> products, BigDecimal price) {
        return RestaurantApprovalRequestModel.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .restaurantId(restaurantId)
                .orderId(orderId)
                .restaurantOrderStatus(restaurantOrderStatus)
                .products(products)
                .price(price)
                .createdAt(Instant.now())
                .build();
    }
}
