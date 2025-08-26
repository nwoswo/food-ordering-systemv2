package com.food.ordering.system.restaurant.service.adapter.inbound.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/restaurants", produces = "application/vnd.api.v1+json")
public class RestaurantController {

    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        log.info("Health check requested for Restaurant Service");
        
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("status", "UP");
        healthResponse.put("service", "restaurant-service");
        healthResponse.put("timestamp", java.time.LocalDateTime.now());
        healthResponse.put("version", "1.0.0");
        
        return ResponseEntity.ok(healthResponse);
    }
}
