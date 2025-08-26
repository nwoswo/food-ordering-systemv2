package com.food.ordering.system.restaurant.service.application.mapper;

import com.food.ordering.system.common.domain.valueobject.Money;
import com.food.ordering.system.common.domain.valueobject.OrderId;
import com.food.ordering.system.common.domain.valueobject.OrderStatus;
import com.food.ordering.system.common.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.application.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.model.entities.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.model.entities.Product;
import com.food.ordering.system.restaurant.service.domain.model.entities.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest
                                                                             restaurantApprovalRequest) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(OrderDetail.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                product -> Product.builder()
                                        .productId(new com.food.ordering.system.common.domain.valueobject.ProductId(UUID.fromString(product.getId())))
                                        .quantity(product.getQuantity())
                                        .build())
                                .collect(Collectors.toList()))
                        .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }
}
