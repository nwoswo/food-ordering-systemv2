package com.food.ordering.system.restaurant.service.domain.service;

import com.food.ordering.system.common.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.restaurant.service.domain.model.entities.Restaurant;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages,
                                     DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
                                     DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher);
}
