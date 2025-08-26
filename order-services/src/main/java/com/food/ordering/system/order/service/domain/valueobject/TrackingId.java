package com.food.ordering.system.order.service.domain.valueobject;

import com.food.ordering.system.common.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
