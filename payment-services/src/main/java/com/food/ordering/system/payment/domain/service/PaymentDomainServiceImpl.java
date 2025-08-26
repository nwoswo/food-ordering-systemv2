package com.food.ordering.system.payment.service.domain.service;

import static com.food.ordering.system.common.domain.DomainConstants.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.food.ordering.system.common.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.common.domain.valueobject.Money;
import com.food.ordering.system.common.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.model.entities.CreditEntry;
import com.food.ordering.system.payment.service.domain.model.entities.CreditHistory;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.model.valueobjects.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.model.valueobjects.TransactionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)),
                    paymentCompletedEventDomainEventPublisher);
        } else {
            log.info("Payment initiation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages,
                    paymentFailedEventDomainEventPublisher);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)),
                    paymentCancelledEventDomainEventPublisher);
        } else {
            log.info("Payment cancellation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages,
                    paymentFailedEventDomainEventPublisher);
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Customer with id: {} doesn't have enough credit for payment!", payment.getCustomerId().getValue());
            failureMessages.add("Customer with id=" + payment.getCustomerId().getValue() +
                    " doesn't have enough credit for payment!");
        }
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(Payment payment,
            List<CreditHistory> creditHistories,
            TransactionType transactionType) {
        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .amount(payment.getPrice())
                .transactionType(transactionType)
                .build());
    }

    private void validateCreditHistory(CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages) {
        Money totalCreditHistory = creditHistories.stream()
                .filter(creditHistory -> creditHistory.getTransactionType() == TransactionType.CREDIT)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);

        Money totalDebitHistory = creditHistories.stream()
                .filter(creditHistory -> creditHistory.getTransactionType() == TransactionType.DEBIT)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory.add(creditEntry.getTotalCreditAmount()))) {
            log.error("Credit history is invalid for customer id: {}!", creditEntry.getCustomerId().getValue());
            failureMessages.add("Credit history is invalid for customer id: " + creditEntry.getCustomerId().getValue());
        }
    }
}
