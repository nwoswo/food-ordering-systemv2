package com.food.ordering.system.consulta.infrastructure.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.consulta.dataaccess.entity.OrderOutboxEntity;
import com.food.ordering.system.consulta.dataaccess.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxKafkaConsumer {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-OrderCreatedEvent",
            groupId = "consulta-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderOutboxEvent(String message) {
        try {
            log.info("Received message from OrderCreatedEvent topic: {}", message);
            
            // The message might be double-encoded JSON string, so we need to parse it twice
            String actualMessage = message;
            if (message.startsWith("\"") && message.endsWith("\"")) {
                // Remove outer quotes and unescape
                actualMessage = message.substring(1, message.length() - 1)
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
            
            JsonNode jsonNode = objectMapper.readTree(actualMessage);
            
            // The message structure is the direct event data:
            // {
            //   "price": 200.00,
            //   "orderId": "uuid",
            //   "customerId": "uuid",
            //   "orderStatus": "PENDING",
            //   "restaurantId": "uuid"
            // }
            
            // Create OrderOutboxEntity from the message
            OrderOutboxEntity orderOutboxEntity = OrderOutboxEntity.builder()
                    .id(UUID.randomUUID()) // Generate new ID for our service
                    .aggregateId(UUID.fromString(jsonNode.get("orderId").asText()))
                    .aggregateType("Order")
                    .eventType("OrderCreatedEvent")
                    .eventData(actualMessage)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .orderId(UUID.fromString(jsonNode.get("orderId").asText()))
                    .customerId(UUID.fromString(jsonNode.get("customerId").asText()))
                    .restaurantId(UUID.fromString(jsonNode.get("restaurantId").asText()))
                    .price(jsonNode.get("price").asDouble())
                    .orderStatus(jsonNode.get("orderStatus").asText())
                    .build();
            
            // Save to database
            orderOutboxRepository.save(orderOutboxEntity);
            log.info("Saved order outbox event with ID: {}", orderOutboxEntity.getId());
            
        } catch (Exception e) {
            log.error("Error processing Debezium message: {}", message, e);
        }
    }
}
