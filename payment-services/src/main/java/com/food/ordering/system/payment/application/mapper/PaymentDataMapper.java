package com.food.ordering.system.payment.service.application.mapper;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.common.domain.valueobject.Money;
import com.food.ordering.system.common.domain.valueobject.OrderId;
import com.food.ordering.system.payment.service.application.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }
}
