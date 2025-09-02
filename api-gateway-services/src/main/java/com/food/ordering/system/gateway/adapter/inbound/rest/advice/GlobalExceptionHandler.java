package com.food.ordering.system.gateway.adapter.inbound.rest.advice;

import com.food.ordering.system.gateway.adapter.inbound.rest.dto.FallbackResponseDto;
import com.food.ordering.system.gateway.adapter.inbound.rest.mapper.FallbackResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final FallbackResponseMapper fallbackResponseMapper;
    
    public GlobalExceptionHandler(FallbackResponseMapper fallbackResponseMapper) {
        this.fallbackResponseMapper = fallbackResponseMapper;
    }
    
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<FallbackResponseDto>> handleWebClientResponseException(WebClientResponseException ex) {
        FallbackResponseDto response = fallbackResponseMapper.toFallbackResponseDto(
                "Service temporarily unavailable",
                "SERVICE_UNAVAILABLE",
                "api-gateway"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<FallbackResponseDto>> handleGenericException(Exception ex) {
        FallbackResponseDto response = fallbackResponseMapper.toFallbackResponseDto(
                "Internal server error",
                "INTERNAL_ERROR",
                "api-gateway"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
