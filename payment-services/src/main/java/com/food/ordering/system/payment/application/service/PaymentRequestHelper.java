package com.food.ordering.system.payment.service.application.service;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.application.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.model.entities.CreditEntry;
import com.food.ordering.system.payment.service.domain.model.entities.CreditHistory;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;
import com.food.ordering.system.payment.service.domain.model.events.PaymentEvent;
import com.food.ordering.system.payment.service.application.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.application.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.application.ports.out.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.application.ports.out.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.application.ports.out.PaymentFailedMessagePublisher;
import com.food.ordering.system.payment.service.application.ports.out.CreditEntryRepository;
import com.food.ordering.system.payment.service.application.ports.out.CreditHistoryRepository;
import com.food.ordering.system.payment.service.application.ports.out.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
    private final PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher,
                                PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher,
                                PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
        this.paymentFailedEventDomainEventPublisher = paymentFailedEventDomainEventPublisher;
        this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages,
                        paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment cancel event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry,
                creditHistories, failureMessages, paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.warn("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " +
                    customerId.getValue());
        }
        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.warn("Could not find credit history for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for customer: " +
                    customerId.getValue());
        }
        return creditHistories.get();
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }
}
