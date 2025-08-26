package com.food.ordering.system.common.domain.event;

public interface DomainEvent<T> {
    void fire();
}
