package com.food.ordering.system.restaurant.service.application.service;

import com.food.ordering.system.restaurant.service.application.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.application.exception.RestaurantApplicationServiceException;
import com.food.ordering.system.restaurant.service.application.mapper.RestaurantDataMapper;
import com.food.ordering.system.restaurant.service.application.ports.out.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.application.ports.out.OrderRejectedMessagePublisher;
import com.food.ordering.system.restaurant.service.application.ports.out.RestaurantRepository;
import com.food.ordering.system.restaurant.service.domain.model.entities.Restaurant;
import com.food.ordering.system.restaurant.service.domain.model.events.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.service.RestaurantDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovedMessagePublisher orderApprovedEventDomainEventPublisher;
    private final OrderRejectedMessagePublisher orderRejectedEventDomainEventPublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovedMessagePublisher orderApprovedEventDomainEventPublisher,
                                           OrderRejectedMessagePublisher orderRejectedEventDomainEventPublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
        this.orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant =
                restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        Restaurant restaurantResult = findRestaurantWithFailureMessages(restaurant, failureMessages);
        if (!failureMessages.isEmpty()) {
            restaurant.setActive(false);
        }

        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages,
                orderApprovedEventDomainEventPublisher, orderRejectedEventDomainEventPublisher);

        return orderApprovalEvent;
    }

    private Restaurant findRestaurantWithFailureMessages(Restaurant restaurant, List<String> failureMessages) {
        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
        if (restaurantResult.isEmpty()) {
            log.error("Restaurant with id " + restaurant.getId().getValue() + " is not found!");
            failureMessages.add("Restaurant with id " + restaurant.getId().getValue() + " is not found!");
            return restaurant;
        }

        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product ->
                restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                    if (p.getId().equals(product.getId())) {
                        product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                    }
                }));
        return restaurant;
    }
}
