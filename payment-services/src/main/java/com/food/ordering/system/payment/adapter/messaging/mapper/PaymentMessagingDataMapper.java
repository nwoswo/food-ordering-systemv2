package com.food.ordering.system.payment.service.adapter.messaging.mapper;

import com.food.ordering.system.common.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;
import com.food.ordering.system.payment.service.application.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestModelToPaymentRequest(PaymentRequestModel paymentRequestModel) {
        return PaymentRequest.builder()
                .id(paymentRequestModel.getId())
                .sagaId(paymentRequestModel.getSagaId())
                .customerId(paymentRequestModel.getCustomerId())
                .orderId(paymentRequestModel.getOrderId())
                .price(paymentRequestModel.getPrice())
                .createdAt(paymentRequestModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestModel.getPaymentOrderStatus().name()))
                .build();
    }

    public PaymentResponseModel paymentCompletedEventToPaymentResponseModel(PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .paymentId(paymentCompletedEvent.getPayment().getId().getValue())
                .customerId(paymentCompletedEvent.getPayment().getCustomerId().getValue())
                .orderId(paymentCompletedEvent.getPayment().getOrderId().getValue())
                .price(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentCompletedEvent.getCreatedAt().toInstant())
                .paymentStatus(PaymentResponseModel.PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().name()))
                .failureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseModel paymentCancelledEventToPaymentResponseModel(PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .paymentId(paymentCancelledEvent.getPayment().getId().getValue())
                .customerId(paymentCancelledEvent.getPayment().getCustomerId().getValue())
                .orderId(paymentCancelledEvent.getPayment().getOrderId().getValue())
                .price(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentCancelledEvent.getCreatedAt().toInstant())
                .paymentStatus(PaymentResponseModel.PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().name()))
                .failureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseModel paymentFailedEventToPaymentResponseModel(PaymentFailedEvent paymentFailedEvent) {
        return PaymentResponseModel.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .paymentId(paymentFailedEvent.getPayment().getId().getValue())
                .customerId(paymentFailedEvent.getPayment().getCustomerId().getValue())
                .orderId(paymentFailedEvent.getPayment().getOrderId().getValue())
                .price(paymentFailedEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentFailedEvent.getCreatedAt().toInstant())
                .paymentStatus(PaymentResponseModel.PaymentStatus.valueOf(paymentFailedEvent.getPayment().getPaymentStatus().name()))
                .failureMessages(paymentFailedEvent.getFailureMessages())
                .build();
    }
}
