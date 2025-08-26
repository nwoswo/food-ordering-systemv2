package com.food.ordering.system.order.service.dataaccess.order.converter;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus != null ? orderStatus.name() : null;
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? OrderStatus.valueOf(dbData) : null;
    }
}

