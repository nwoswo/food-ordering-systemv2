package com.food.ordering.system.consulta.adapter.in.rest.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderOutboxResponseDTO {
    private UUID id;
    private UUID aggregateId;
    private String aggregateType;
    private String eventType;
    // Este campo será un objeto JSON en la respuesta final
    private JsonNode eventData;
    private LocalDateTime createdAt;
    private boolean processed;
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private Double price;
    private String orderStatus;
    private UUID trackingId;
}