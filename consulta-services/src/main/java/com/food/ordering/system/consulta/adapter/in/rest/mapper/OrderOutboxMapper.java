package com.food.ordering.system.consulta.adapter.in.rest.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.consulta.adapter.in.rest.dto.OrderOutboxResponseDTO;
import com.food.ordering.system.consulta.adapter.out.dataaccess.entity.OrderOutboxEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class OrderOutboxMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    // MapStruct generará la implementación de este método, mapeando automáticamente
    // los campos con nombres iguales. Para el campo 'eventData', usará el método
    // 'stringToJsonNode' que definimos abajo.
    public abstract OrderOutboxResponseDTO toDto(OrderOutboxEntity entity);

    // Lógica de mapeo personalizada para el campo String -> JsonNode
    protected JsonNode stringToJsonNode(String eventData) {
        if (eventData == null) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(eventData);
        } catch (JsonProcessingException e) {
            log.error("No se pudo parsear eventData a JSON: {}", eventData, e);
            return objectMapper.createObjectNode();
        }
    }
}