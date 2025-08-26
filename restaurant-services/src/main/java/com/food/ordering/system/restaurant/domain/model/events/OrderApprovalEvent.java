package com.food.ordering.system.restaurant.service.domain.model.events;

import com.food.ordering.system.common.domain.event.DomainEvent;
import com.food.ordering.system.common.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.model.entities.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessages;

    public OrderApprovalEvent(OrderApproval orderApproval,
                              RestaurantId restaurantId,
                              ZonedDateTime createdAt,
                              List<String> failureMessages) {
        this.orderApproval = orderApproval;
        this.restaurantId = restaurantId;
        this.createdAt = createdAt;
        this.failureMessages = failureMessages;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
