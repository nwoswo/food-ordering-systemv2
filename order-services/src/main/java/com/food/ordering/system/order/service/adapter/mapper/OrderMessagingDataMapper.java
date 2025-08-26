package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestModel;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseModel;
import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;
import com.food.ordering.system.kafka.stream.model.Product;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestModel orderCreatedEventToPaymentRequestModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestModel.createPaymentRequest(
                UUID.randomUUID().toString(), // sagaId
                order.getCustomerId().getValue().toString(),
                order.getId().getValue().toString(),
                order.getPrice().getAmount()
        );
    }

    public PaymentRequestModel orderCancelledEventToPaymentRequestModel(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        PaymentRequestModel model = PaymentRequestModel.createPaymentRequest(
                UUID.randomUUID().toString(), // sagaId
                order.getCustomerId().getValue().toString(),
                order.getId().getValue().toString(),
                order.getPrice().getAmount()
        );
        // Set status to CANCELLED
        return PaymentRequestModel.builder()
                .id(model.getId())
                .sagaId(model.getSagaId())
                .customerId(model.getCustomerId())
                .orderId(model.getOrderId())
                .price(model.getPrice())
                .createdAt(model.getCreatedAt())
                .paymentOrderStatus(PaymentRequestModel.PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestModel
    orderPaidEventToRestaurantApprovalRequestModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return RestaurantApprovalRequestModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .orderId(order.getId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .products(order.getItems().stream().map(orderItem ->
                        Product.builder()
                                .id(orderItem.getProduct().getId().getValue())
                                .quantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .price(order.getPrice().getAmount())
                .createdAt(orderPaidEvent.getCreatedAt().toInstant())
                .restaurantOrderStatus(RestaurantApprovalRequestModel.RestaurantOrderStatus.PAID)
                .build();
    }

    public PaymentResponse paymentResponseModelToPaymentResponse(PaymentResponseModel
                                                                             paymentResponseModel) {
        return PaymentResponse.builder()
                .id(paymentResponseModel.getId().toString())
                .sagaId(paymentResponseModel.getSagaId().toString())
                .paymentId(paymentResponseModel.getPaymentId().toString())
                .customerId(paymentResponseModel.getCustomerId().toString())
                .orderId(paymentResponseModel.getOrderId().toString())
                .price(paymentResponseModel.getPrice())
                .createdAt(paymentResponseModel.getCreatedAt())
                .paymentStatus(com.food.ordering.system.common.domain.valueobject.PaymentStatus.valueOf(
                        paymentResponseModel.getPaymentStatus().name()))
                .failureMessages(paymentResponseModel.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse
    approvalResponseModelToApprovalResponse(RestaurantApprovalResponseModel
                                                        restaurantApprovalResponseModel) {
        return RestaurantApprovalResponse.builder()
                .id(restaurantApprovalResponseModel.getId().toString())
                .sagaId(restaurantApprovalResponseModel.getSagaId().toString())
                .restaurantId(restaurantApprovalResponseModel.getRestaurantId().toString())
                .orderId(restaurantApprovalResponseModel.getOrderId().toString())
                .createdAt(restaurantApprovalResponseModel.getCreatedAt())
                .orderApprovalStatus(com.food.ordering.system.common.domain.valueobject.OrderApprovalStatus.valueOf(
                        restaurantApprovalResponseModel.getOrderApprovalStatus().name()))
                .failureMessages(restaurantApprovalResponseModel.getFailureMessages())
                .build();
    }
}
