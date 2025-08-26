package com.food.ordering.system.restaurant.service.messaging.mapper;


import com.food.ordering.system.common.domain.valueobject.ProductId;
import com.food.ordering.system.common.domain.valueobject.RestaurantOrderStatus;

import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestModel;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseModel;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {
    public RestaurantApprovalResponseModel
    orderApprovedEventToRestaurantApprovalResponseModel(OrderApprovedEvent orderApprovedEvent) {
        return RestaurantApprovalResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .orderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue())
                .restaurantId(orderApprovedEvent.getRestaurantId().getValue())
                .createdAt(orderApprovedEvent.getCreatedAt().toInstant())
                .orderApprovalStatus(RestaurantApprovalResponseModel.OrderApprovalStatus.valueOf(orderApprovedEvent.
                        getOrderApproval().getApprovalStatus().name()))
                .failureMessages(orderApprovedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponseModel
    orderRejectedEventToRestaurantApprovalResponseModel(OrderRejectedEvent orderRejectedEvent) {
        return RestaurantApprovalResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .orderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue())
                .restaurantId(orderRejectedEvent.getRestaurantId().getValue())
                .createdAt(orderRejectedEvent.getCreatedAt().toInstant())
                .orderApprovalStatus(RestaurantApprovalResponseModel.OrderApprovalStatus.valueOf(orderRejectedEvent.
                        getOrderApproval().getApprovalStatus().name()))
                .failureMessages(orderRejectedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequest
    restaurantApprovalRequestModelToRestaurantApproval(RestaurantApprovalRequestModel
                                                                   restaurantApprovalRequestModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestModel.getId().toString())
                .sagaId(restaurantApprovalRequestModel.getSagaId().toString())
                .restaurantId(restaurantApprovalRequestModel.getRestaurantId().toString())
                .orderId(restaurantApprovalRequestModel.getOrderId().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestModel
                        .getRestaurantOrderStatus().name()))
                .products(restaurantApprovalRequestModel.getProducts()
                        .stream().map(model ->
                                Product.builder()
                                        .productId(new ProductId(model.getId()))
                                        .quantity(model.getQuantity())
                                        .build())
                        .collect(Collectors.toList()))
                .price(restaurantApprovalRequestModel.getPrice())
                .createdAt(restaurantApprovalRequestModel.getCreatedAt())
                .build();
    }
}
