package com.food.ordering.system.restaurant.service.domain.model.valueobjects;

import com.food.ordering.system.common.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
