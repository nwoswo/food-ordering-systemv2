package com.food.ordering.system.gateway.application.services;

import com.food.ordering.system.gateway.adapter.inbound.rest.dto.FallbackResponseDto;
import com.food.ordering.system.gateway.application.port.in.FallbackServicePort;
import com.food.ordering.system.gateway.adapter.inbound.rest.mapper.FallbackResponseMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FallbackService implements FallbackServicePort {
    
    private final FallbackResponseMapper fallbackResponseMapper;
    
    public FallbackService(FallbackResponseMapper fallbackResponseMapper) {
        this.fallbackResponseMapper = fallbackResponseMapper;
    }
    
    @Override
    public Mono<FallbackResponseDto> getOrderServiceFallback() {
        return Mono.just(fallbackResponseMapper.toFallbackResponseDto(
                "Order Service is currently unavailable",
                "SERVICE_UNAVAILABLE",
                "order-service"
        ));
    }
    
    @Override
    public Mono<FallbackResponseDto> getPaymentServiceFallback() {
        return Mono.just(fallbackResponseMapper.toFallbackResponseDto(
                "Payment Service is currently unavailable",
                "SERVICE_UNAVAILABLE",
                "payment-service"
        ));
    }
    
    @Override
    public Mono<FallbackResponseDto> getRestaurantServiceFallback() {
        return Mono.just(fallbackResponseMapper.toFallbackResponseDto(
                "Restaurant Service is currently unavailable",
                "SERVICE_UNAVAILABLE",
                "restaurant-service"
        ));
    }
    
    @Override
    public Mono<FallbackResponseDto> getCustomerServiceFallback() {
        return Mono.just(fallbackResponseMapper.toFallbackResponseDto(
                "Customer Service is currently unavailable",
                "SERVICE_UNAVAILABLE",
                "customer-service"
        ));
    }
    
    @Override
    public Mono<FallbackResponseDto> getGatewayHealth() {
        return Mono.just(fallbackResponseMapper.toFallbackResponseDto(
                "API Gateway is running",
                "UP",
                "api-gateway"
        ));
    }
}
