package com.food.ordering.system.order.service.application.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component; 
import org.springframework.transaction.annotation.Transactional;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.infrastructure.order.adapter.OutboxService;
import com.food.ordering.system.order.service.domain.OrderDomainService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderCreateHelper {

  private final OrderDomainService orderDomainService;

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final RestaurantRepository restaurantRepository;

  private final OrderDataMapper orderDataMapper;

  private final OutboxService outboxService;
  private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

  private final ObjectMapper objectMapper;

  public OrderCreateHelper(OrderDomainService orderDomainService,
                           OrderRepository orderRepository,
                           CustomerRepository customerRepository,
                           RestaurantRepository restaurantRepository,
                           OrderDataMapper orderDataMapper,
                           OutboxService outboxService,
                           OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher,
                           ObjectMapper objectMapper) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderDataMapper = orderDataMapper;
    this.outboxService = outboxService;
    this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
    checkCustomer(createOrderCommand.getCustomerId());
    Restaurant restaurant = checkRestaurant(createOrderCommand);
    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);

    // Update order items with actual product prices from restaurant
    updateOrderItemsWithProductPrices(order, restaurant);

    OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant, orderCreatedPaymentRequestMessagePublisher);
    saveOrder(order);

    // Save event to outbox for reliable messaging
    saveEventToOutbox(orderCreatedEvent);

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
    // Create a map of products for efficient lookup (O(N) operation).
    Map<UUID, Product> restaurantProductMap = restaurant.getProducts().stream()
        .collect(Collectors.toMap(product -> product.getId().getValue(), Function.identity()));

    // Iterate through order items and update prices (O(M) operation).
    order.getItems().forEach(orderItem -> {
      UUID productId = orderItem.getProduct().getId().getValue();
      Product restaurantProduct = restaurantProductMap.get(productId);

      if (restaurantProduct != null) {
        orderItem.getProduct().updateWithConfirmedNameAndPrice(
            restaurantProduct.getName(),
            restaurantProduct.getPrice()
        );
      } else {
        // This case should ideally not happen if validation is correct upstream.
        // Logging it as a warning is important for debugging data integrity issues.
        log.warn("Product with id: {} not found in restaurant: {}. Price not updated for this item.",
            productId, restaurant.getId().getValue());
      }
    });
  }

  private void saveEventToOutbox(OrderCreatedEvent orderCreatedEvent) {
    try {
      Order order = orderCreatedEvent.getOrder();

      // Using a Map to build the payload is more readable and robust than manual string formatting.
      // It relies on a proper JSON library (like Jackson) to handle serialization correctly.
      Map<String, Object> payload = new LinkedHashMap<>();
      payload.put("orderId", order.getId().getValue());
      payload.put("customerId", order.getCustomerId().getValue());
      payload.put("restaurantId", order.getRestaurantId().getValue());
      payload.put("price", order.getPrice().getAmount());
      payload.put("orderStatus", order.getOrderStatus().name());
      payload.put("trackingid", order.getTrackingId().getValue());

      String eventData = objectMapper.writeValueAsString(payload);

      outboxService.saveEvent(
        order.getTrackingId().getValue(),
        "Order",
        "OrderCreatedEvent",
        eventData
      );

      log.info("OrderCreatedEvent saved to outbox for order: {}", order.getId().getValue());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize OrderCreatedEvent payload for outbox", e);
    } catch (Exception e) {
      log.error("Failed to save event to outbox", e);
      // Don't throw exception to avoid breaking the transaction
    }
  }
}
