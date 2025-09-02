package com.food.ordering.system.gateway.application.port.in;

import com.food.ordering.system.gateway.adapter.inbound.rest.dto.FallbackResponseDto;
import reactor.core.publisher.Mono;

public interface FallbackServicePort {
    
    Mono<FallbackResponseDto> getOrderServiceFallback();
    
    Mono<FallbackResponseDto> getPaymentServiceFallback();
    
    Mono<FallbackResponseDto> getRestaurantServiceFallback();
    
    Mono<FallbackResponseDto> getCustomerServiceFallback();
    
    Mono<FallbackResponseDto> getGatewayHealth();
}
