package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("quantity")
    private Integer quantity;
}
