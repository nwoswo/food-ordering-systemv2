package com.food.ordering.system.gateway.adapter.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FallbackResponseDto {
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String service;
    private String version;
}
