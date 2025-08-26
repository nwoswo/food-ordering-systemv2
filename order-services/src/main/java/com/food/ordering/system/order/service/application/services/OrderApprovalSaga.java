package com.food.ordering.system.order.service.domain;

import org.springframework.stereotype.Component;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

  private final OrderDomainService orderDomainService;
  private final OrderSagaHelper orderSagaHelper;
  private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

  @Override
  public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Processing restaurant approval for order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrderById(restaurantApprovalResponse.getOrderId());
    orderDomainService.approveOrder(order);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is approved successfully", order.getId().getValue());
    return EmptyEvent.INSTANCE;
  }

  @Override
  public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("Rolling back order approval for order with id: {}", restaurantApprovalResponse.getOrderId());
    Order order = orderSagaHelper.findOrderById(restaurantApprovalResponse.getOrderId());
    OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(order,
        restaurantApprovalResponse.getFailureMessages(),
        orderCancelledPaymentRequestMessagePublisher);
    orderSagaHelper.saveOrder(order);
    log.info("Order with id: {} is cancelled successfully", order.getId().getValue());
    return domainEvent;
  }

}
