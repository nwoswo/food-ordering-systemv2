package com.food.ordering.system.payment.service.domain.service;

import com.food.ordering.system.common.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.model.entities.CreditEntry;
import com.food.ordering.system.payment.service.domain.model.entities.CreditHistory;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent>
                                                    paymentCompletedEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages, DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);
}
