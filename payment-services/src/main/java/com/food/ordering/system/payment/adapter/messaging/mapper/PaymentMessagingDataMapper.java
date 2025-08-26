package com.food.ordering.system.payment.service.adapter.messaging.mapper;

import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;
import com.food.ordering.system.payment.service.application.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.model.events.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

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
                .paymentOrderStatus(paymentRequestModel.getPaymentOrderStatus())
                .build();
    }

    public PaymentResponseModel paymentCompletedEventToPaymentResponseModel(PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseModel.builder()
                .id(paymentCompletedEvent.getPayment().getId().getValue().toString())
                .sagaId(paymentCompletedEvent.getPayment().getId().getValue().toString())
                .paymentId(paymentCompletedEvent.getPayment().getId().getValue().toString())
                .customerId(paymentCompletedEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentCompletedEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentCompletedEvent.getCreatedAt())
                .paymentStatus(paymentCompletedEvent.getPayment().getPaymentStatus())
                .failureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseModel paymentCancelledEventToPaymentResponseModel(PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseModel.builder()
                .id(paymentCancelledEvent.getPayment().getId().getValue().toString())
                .sagaId(paymentCancelledEvent.getPayment().getId().getValue().toString())
                .paymentId(paymentCancelledEvent.getPayment().getId().getValue().toString())
                .customerId(paymentCancelledEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentCancelledEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentCancelledEvent.getCreatedAt())
                .paymentStatus(paymentCancelledEvent.getPayment().getPaymentStatus())
                .failureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseModel paymentFailedEventToPaymentResponseModel(PaymentFailedEvent paymentFailedEvent) {
        return PaymentResponseModel.builder()
                .id(paymentFailedEvent.getPayment().getId().getValue().toString())
                .sagaId(paymentFailedEvent.getPayment().getId().getValue().toString())
                .paymentId(paymentFailedEvent.getPayment().getId().getValue().toString())
                .customerId(paymentFailedEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentFailedEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentFailedEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentFailedEvent.getCreatedAt())
                .paymentStatus(paymentFailedEvent.getPayment().getPaymentStatus())
                .failureMessages(paymentFailedEvent.getFailureMessages())
                .build();
    }
}
