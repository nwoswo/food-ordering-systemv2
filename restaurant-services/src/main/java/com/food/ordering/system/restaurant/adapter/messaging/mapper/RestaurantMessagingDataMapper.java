package com.food.ordering.system.restaurant.service.adapter.messaging.mapper;

import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestModel;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseModel;
import com.food.ordering.system.restaurant.service.application.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderRejectedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {

    public RestaurantApprovalRequest restaurantApprovalRequestModelToRestaurantApproval(RestaurantApprovalRequestModel restaurantApprovalRequestModel) {
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestModel.getId())
                .sagaId(restaurantApprovalRequestModel.getSagaId())
                .restaurantId(restaurantApprovalRequestModel.getRestaurantId())
                .orderId(restaurantApprovalRequestModel.getOrderId())
                .restaurantOrderStatus(restaurantApprovalRequestModel.getRestaurantOrderStatus())
                .products(restaurantApprovalRequestModel.getProducts().stream().map(product ->
                        RestaurantApprovalRequest.Product.builder()
                                .id(product.getId())
                                .quantity(product.getQuantity())
                                .build()).collect(Collectors.toList()))
                .price(restaurantApprovalRequestModel.getPrice())
                .createdAt(restaurantApprovalRequestModel.getCreatedAt())
                .build();
    }

    public RestaurantApprovalResponseModel orderApprovedEventToRestaurantApprovalResponseModel(OrderApprovedEvent orderApprovedEvent) {
        return RestaurantApprovalResponseModel.builder()
                .id(orderApprovedEvent.getOrderApproval().getId().getValue().toString())
                .sagaId(orderApprovedEvent.getOrderApproval().getId().getValue().toString())
                .restaurantId(orderApprovedEvent.getRestaurantId().getValue().toString())
                .orderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue().toString())
                .createdAt(orderApprovedEvent.getCreatedAt())
                .orderApprovalStatus(orderApprovedEvent.getOrderApproval().getApprovalStatus())
                .failureMessages(orderApprovedEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponseModel orderRejectedEventToRestaurantApprovalResponseModel(OrderRejectedEvent orderRejectedEvent) {
        return RestaurantApprovalResponseModel.builder()
                .id(orderRejectedEvent.getOrderApproval().getId().getValue().toString())
                .sagaId(orderRejectedEvent.getOrderApproval().getId().getValue().toString())
                .restaurantId(orderRejectedEvent.getRestaurantId().getValue().toString())
                .orderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString())
                .createdAt(orderRejectedEvent.getCreatedAt())
                .orderApprovalStatus(orderRejectedEvent.getOrderApproval().getApprovalStatus())
                .failureMessages(orderRejectedEvent.getFailureMessages())
                .build();
    }
}
