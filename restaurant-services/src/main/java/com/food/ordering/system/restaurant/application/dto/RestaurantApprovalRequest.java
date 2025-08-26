package com.food.ordering.system.restaurant.service.application.dto;

import com.food.ordering.system.common.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {
    private String id;
    private String sagaId;
    private String restaurantId;
    private String orderId;
    private OrderStatus restaurantOrderStatus;
    private List<Product> products;
    private BigDecimal price;
    private java.time.Instant createdAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Product {
        private String id;
        private int quantity;
    }
}
