package com.food.ordering.system.payment.service.domain.model.events;

import com.food.ordering.system.common.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentCompletedEvent(Payment payment,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
        super(payment, createdAt, List.of());
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
