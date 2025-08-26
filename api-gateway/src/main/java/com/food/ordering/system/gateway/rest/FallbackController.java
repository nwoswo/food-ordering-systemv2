package com.food.ordering.system.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/order-service")
    public Mono<ResponseEntity<Map<String, Object>>> orderServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order Service is currently unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/payment-service")
    public Mono<ResponseEntity<Map<String, Object>>> paymentServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment Service is currently unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/restaurant-service")
    public Mono<ResponseEntity<Map<String, Object>>> restaurantServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Restaurant Service is currently unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/customer-service")
    public Mono<ResponseEntity<Map<String, Object>>> customerServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer Service is currently unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> gatewayHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        response.put("message", "API Gateway is running");
        
        return Mono.just(ResponseEntity.ok(response));
    }
}
