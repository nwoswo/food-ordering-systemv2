package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.domain.valueobject.OrderId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentResponseHelper {

    private final OrderDomainService orderDomainService;
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    public PaymentResponseHelper(OrderDomainService orderDomainService,
                                 OrderDataMapper orderDataMapper,
                                 OrderRepository orderRepository,
                                 OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher) {
        this.orderDomainService = orderDomainService;
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
        this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
    }

    @Transactional
    public void persistPaymentResponse(PaymentResponse paymentResponse) {
        log.info("Processing payment response for order id: {}", paymentResponse.getOrderId());
        
        Order order = findOrder(paymentResponse.getOrderId());
        
        if (paymentResponse.getPaymentStatus().name().equals("COMPLETED")) {
            log.info("Payment completed for order id: {}", paymentResponse.getOrderId());
            OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
            saveOrder(order);
            fireEvent(orderPaidEvent);
        } else {
            log.info("Payment failed for order id: {}", paymentResponse.getOrderId());
            List<String> failureMessages = new ArrayList<>(paymentResponse.getFailureMessages());
            orderDomainService.cancelOrder(order, failureMessages);
            saveOrder(order);
        }
    }

    private Order findOrder(String orderId) {
        Optional<Order> orderResult = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderResult.isEmpty()) {
            log.error("Order with id {} not found!", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " not found!");
        }
        return orderResult.get();
    }

    private void saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        log.info("Order is saved with id: {}", savedOrder.getId().getValue());
    }

    private void fireEvent(OrderPaidEvent orderPaidEvent) {
        log.info("Publishing OrderPaidEvent for order id: {}", orderPaidEvent.getOrder().getId().getValue());
        orderPaidEvent.fire();
    }
}
