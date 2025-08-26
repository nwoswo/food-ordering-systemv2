package com.food.ordering.system.payment.service.infrastructure.persistence.mappers;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.common.domain.valueobject.Money;
import com.food.ordering.system.common.domain.valueobject.OrderId;
import com.food.ordering.system.payment.service.infrastructure.persistence.entities.PaymentEntity;
import com.food.ordering.system.payment.service.domain.model.entities.Payment;
import com.food.ordering.system.payment.service.domain.model.valueobjects.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}
