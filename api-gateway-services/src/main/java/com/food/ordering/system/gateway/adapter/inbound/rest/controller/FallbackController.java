package com.food.ordering.system.gateway.adapter.inbound.rest.controller;

import com.food.ordering.system.gateway.adapter.inbound.rest.dto.FallbackResponseDto;
import com.food.ordering.system.gateway.application.port.in.FallbackServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private final FallbackServicePort fallbackServicePort;

    public FallbackController(FallbackServicePort fallbackServicePort) {
        this.fallbackServicePort = fallbackServicePort;
    }

    @GetMapping("/order-service")
    public Mono<ResponseEntity<FallbackResponseDto>> orderServiceFallback() {
        return fallbackServicePort.getOrderServiceFallback()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/payment-service")
    public Mono<ResponseEntity<FallbackResponseDto>> paymentServiceFallback() {
        return fallbackServicePort.getPaymentServiceFallback()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/restaurant-service")
    public Mono<ResponseEntity<FallbackResponseDto>> restaurantServiceFallback() {
        return fallbackServicePort.getRestaurantServiceFallback()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/customer-service")
    public Mono<ResponseEntity<FallbackResponseDto>> customerServiceFallback() {
        return fallbackServicePort.getCustomerServiceFallback()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<FallbackResponseDto>> gatewayHealth() {
        return fallbackServicePort.getGatewayHealth()
                .map(ResponseEntity::ok);
    }
}
