package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.order.service.domain.entity.Order.*;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

	private final OrderPaymentSaga orderPaymentSaga;
	private OrderPaidEvent process;

	@Override
	public void paymentCompleted(PaymentResponse paymentResponse) {
		OrderPaidEvent domainEvent = orderPaymentSaga.process(paymentResponse);
		log.info("Publishing OrderPaiEvent for order id: {}", paymentResponse.getOrderId());
		domainEvent.fire();
	}

	@Override
	public void paymentCancelled(PaymentResponse paymentResponse) {
		orderPaymentSaga.rollback(paymentResponse);
		log.info("Order is roll backend for order id: {} with failure messages: {}",
			paymentResponse.getOrderId(),
			String.join(FAILURE_MESSAGE_DELIMITER, paymentResponse.getFailureMessages()));
	}
}
