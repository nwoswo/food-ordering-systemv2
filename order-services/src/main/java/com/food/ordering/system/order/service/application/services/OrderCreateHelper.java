package com.food.ordering.system.order.service.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedEventDomainEventPublisher;

    public OrderCreateHelper(OrderDomainService orderDomainService,
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            RestaurantRepository restaurantRepository,
            OrderDataMapper orderDataMapper,
            OrderCreatedPaymentRequestMessagePublisher orderCreatedEventDomainEventPublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        
        // Update order items with actual product prices from restaurant
        updateOrderItemsWithProductPrices(order, restaurant);
        
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant,
                orderCreatedEventDomainEventPublisher);
        saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: " +
                    createOrderCommand.getRestaurantId());
        }
        return optionalRestaurant.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customer);
        }
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }
    
    private void updateOrderItemsWithProductPrices(Order order, Restaurant restaurant) {
        System.out.println("DEBUG: === updateOrderItemsWithProductPrices called ===");
        System.out.println("DEBUG: Restaurant has " + restaurant.getProducts().size() + " products");
        restaurant.getProducts().forEach(product -> {
            System.out.println("DEBUG: Restaurant product: " + product.getId().getValue() + " - " + product.getName() + " - " + product.getPrice().getAmount());
        });
        
        order.getItems().forEach(orderItem -> {
            System.out.println("DEBUG: Processing order item with product ID: " + orderItem.getProduct().getId().getValue());
            System.out.println("DEBUG: Order item price before update: " + orderItem.getPrice().getAmount());
            
            restaurant.getProducts().stream()
                    .filter(product -> product.getId().equals(orderItem.getProduct().getId()))
                    .findFirst()
                    .ifPresent(product -> {
                        System.out.println("DEBUG: Found matching product: " + product.getName() + " with price: " + product.getPrice().getAmount());
                        // Update the product in the order item with the actual product from restaurant
                        orderItem.getProduct().updateWithConfirmedNameAndPrice(product.getName(), product.getPrice());
                        System.out.println("DEBUG: Updated order item product price to: " + orderItem.getProduct().getPrice().getAmount());
                    });
        });
        System.out.println("DEBUG: === updateOrderItemsWithProductPrices completed ===");
    }
}
