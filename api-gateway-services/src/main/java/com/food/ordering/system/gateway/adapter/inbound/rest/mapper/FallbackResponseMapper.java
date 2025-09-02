package com.food.ordering.system.gateway.adapter.inbound.rest.mapper;

import com.food.ordering.system.gateway.adapter.inbound.rest.dto.FallbackResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class FallbackResponseMapper {
    
    public FallbackResponseDto toFallbackResponseDto(String message, String status, String service) {
        return FallbackResponseDto.builder()
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .service(service)
                .version("1.0.0")
                .build();
    }
    
    public Map<String, Object> toMap(FallbackResponseDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", dto.getMessage());
        map.put("status", dto.getStatus());
        map.put("timestamp", System.currentTimeMillis());
        map.put("service", dto.getService());
        map.put("version", dto.getVersion());
        return map;
    }
}
