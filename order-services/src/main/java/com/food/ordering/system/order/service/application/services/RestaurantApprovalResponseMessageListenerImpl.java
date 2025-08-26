package com.food.ordering.system.order.service.domain;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {

  private final OrderApprovalSaga orderApprovalSaga;

  @Override
  public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
    orderApprovalSaga.process(restaurantApprovalResponse);
    log.info("Order with id: {} has been approved by restaurant with id: {}",
        restaurantApprovalResponse.getOrderId(), restaurantApprovalResponse.getRestaurantId());
  }

  @Override
  public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
    OrderCancelledEvent domainEvent = orderApprovalSaga.rollback(restaurantApprovalResponse);
    log.info("Order with id: {} has been rejected by restaurant with id: {}",
        restaurantApprovalResponse.getOrderId(), restaurantApprovalResponse.getRestaurantId());
    log.info("Order cancelled event published for order id: {}", domainEvent.getOrder().getId());
    domainEvent.fire();
  }
}
