package com.food.ordering.system.payment.service.domain.model.valueobjects;

import com.food.ordering.system.common.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
