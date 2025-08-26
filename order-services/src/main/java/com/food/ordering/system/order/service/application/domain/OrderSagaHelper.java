package com.food.ordering.system.order.service.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.food.ordering.system.common.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaHelper {

  private final OrderRepository orderRepository;

  Order findOrderById(String orderId) {
    Optional<Order> orderResponse = orderRepository.findById(new OrderId(UUID.fromString(orderId)));

    if (orderResponse.isEmpty()) {
      log.error("Order with id: {} not found in the database", orderId);
      throw new OrderNotFoundException("Order with id: " + orderId + " not found in the database");
    }
    return orderResponse.get();
  }

  void saveOrder(Order order) {
    log.info("Saving order with id: {}", order.getId().getValue());
    orderRepository.save(order);
  }
}
