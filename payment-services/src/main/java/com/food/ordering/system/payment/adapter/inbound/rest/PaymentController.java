package com.food.ordering.system.payment.service.adapter.inbound.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/payments", produces = "application/vnd.api.v1+json")
public class PaymentController {

    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        log.info("Health check requested for Payment Service");
        
        Map<String, Object> healthResponse = new HashMap<>();
        healthResponse.put("status", "UP");
        healthResponse.put("service", "payment-service");
        healthResponse.put("timestamp", java.time.LocalDateTime.now());
        healthResponse.put("version", "1.0.0");
        
        return ResponseEntity.ok(healthResponse);
    }
}
