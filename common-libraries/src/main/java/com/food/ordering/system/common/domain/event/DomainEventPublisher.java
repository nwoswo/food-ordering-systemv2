package com.food.ordering.system.common.domain.event.publisher;

import com.food.ordering.system.common.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
