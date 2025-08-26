package com.food.ordering.system.common.saga;

import com.food.ordering.system.common.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {
    S process(T data);
    U rollback(T data);
}
