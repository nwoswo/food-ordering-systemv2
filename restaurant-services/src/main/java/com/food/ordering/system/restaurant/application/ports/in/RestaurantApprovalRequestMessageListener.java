package com.food.ordering.system.restaurant.service.application.ports.in;

import com.food.ordering.system.restaurant.service.application.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {
    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
