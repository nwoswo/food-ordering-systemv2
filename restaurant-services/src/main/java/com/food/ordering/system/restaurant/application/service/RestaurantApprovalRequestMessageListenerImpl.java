package com.food.ordering.system.restaurant.service.application.service;

import org.springframework.stereotype.Service;

import com.food.ordering.system.restaurant.service.application.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.application.ports.in.RestaurantApprovalRequestMessageListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

  private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

  @Override
  public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
    OrderApprovalEvent orderApprovalEvent = restaurantApprovalRequestHelper
        .persistOrderApproval(restaurantApprovalRequest);
    orderApprovalEvent.fire();
  }
}
