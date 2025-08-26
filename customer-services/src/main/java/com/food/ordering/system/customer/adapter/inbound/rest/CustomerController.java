package com.food.ordering.system.customer.adapter.inbound.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        var healthResponse = new java.util.HashMap<String, Object>();
        healthResponse.put("status", "UP");
        healthResponse.put("service", "customer-service");
        healthResponse.put("timestamp", java.time.LocalDateTime.now());
        healthResponse.put("version", "1.0.0");
        
        return ResponseEntity.ok(healthResponse);
    }

    @GetMapping
    public ResponseEntity<String> getCustomers() {
        return ResponseEntity.ok("Customer Service - Get Customers endpoint");
    }
}
