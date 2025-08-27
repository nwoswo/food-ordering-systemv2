package com.food.ordering.system.consulta.infrastructure.rest;

import com.food.ordering.system.consulta.dataaccess.entity.OrderOutboxEntity;
import com.food.ordering.system.consulta.dataaccess.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consulta")
@RequiredArgsConstructor
@Slf4j
public class ConsultaController {

    private final OrderOutboxRepository orderOutboxRepository;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderOutboxEntity> getOrderById(@PathVariable UUID orderId) {
        log.info("Consulting order with ID: {}", orderId);
        
        return orderOutboxRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderOutboxEntity>> getAllOrders() {
        log.info("Consulting all orders");
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/aggregate/{aggregateId}")
    public ResponseEntity<List<OrderOutboxEntity>> getOrdersByAggregateId(@PathVariable UUID aggregateId) {
        log.info("Consulting orders by aggregate ID: {}", aggregateId);
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findByAggregateId(aggregateId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/event-type/{eventType}")
    public ResponseEntity<List<OrderOutboxEntity>> getOrdersByEventType(@PathVariable String eventType) {
        log.info("Consulting orders by event type: {}", eventType);
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findByEventType(eventType);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/processed/{processed}")
    public ResponseEntity<List<OrderOutboxEntity>> getOrdersByProcessedStatus(@PathVariable boolean processed) {
        log.info("Consulting orders by processed status: {}", processed);
        
        List<OrderOutboxEntity> orders = orderOutboxRepository.findByProcessed(processed);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Consulta Service is running!");
    }
}
