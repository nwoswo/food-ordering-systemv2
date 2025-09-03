package com.food.ordering.system.consulta.adapter.in.rest;

import com.food.ordering.system.consulta.adapter.in.rest.dto.OrderOutboxResponseDTO;
import com.food.ordering.system.consulta.adapter.in.rest.mapper.OrderOutboxMapper;
import com.food.ordering.system.consulta.adapter.out.dataaccess.entity.OrderOutboxEntity;
import com.food.ordering.system.consulta.adapter.out.dataaccess.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
@Slf4j
public class ConsultaController {

  private final OrderOutboxRepository orderOutboxRepository;
  private final OrderOutboxMapper orderOutboxMapper;

  @GetMapping("/order/{orderId}")
  public ResponseEntity<OrderOutboxResponseDTO> getOrderById(@PathVariable UUID orderId) {
    log.info("Consulting order with ID: {}", orderId);

    return orderOutboxRepository.findByOrderId(orderId)
        .map(orderOutboxMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/orders")
  public ResponseEntity<List<OrderOutboxResponseDTO>> getAllOrders() {
    log.info("Consulting all orders");

    List<OrderOutboxEntity> orders = orderOutboxRepository.findAll();
    List<OrderOutboxResponseDTO> dtos = orders.stream()
        .map(orderOutboxMapper::toDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/orders/aggregate/{aggregateId}")
  public ResponseEntity<List<OrderOutboxResponseDTO>> getOrdersByAggregateId(@PathVariable UUID aggregateId) {
    log.info("Consulting orders by aggregate ID: {}", aggregateId);

    List<OrderOutboxEntity> orders = orderOutboxRepository.findByAggregateId(aggregateId);
    List<OrderOutboxResponseDTO> dtos = orders.stream()
        .map(orderOutboxMapper::toDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/trackId/{tackingId}")
  public ResponseEntity<OrderOutboxResponseDTO> getOrderByTracking(@PathVariable UUID tackingId) {
    log.info("Consulting getOrderByTracking : {}", tackingId);

    return orderOutboxRepository.findByTrackingId(tackingId)
        .map(orderOutboxMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/health")
  public ResponseEntity<Object> health() {
    var healthResponse = new java.util.HashMap<String, Object>();
    healthResponse.put("status", "UP");
    healthResponse.put("service", "consulta-service");
    healthResponse.put("timestamp", java.time.LocalDateTime.now());
    healthResponse.put("version", "1.0.0");

    return ResponseEntity.ok(healthResponse);
  }

}
