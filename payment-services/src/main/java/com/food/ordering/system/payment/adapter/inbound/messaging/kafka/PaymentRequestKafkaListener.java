package com.food.ordering.system.payment.service.adapter.inbound.messaging.kafka;

import com.food.ordering.system.kafka.stream.consumer.KafkaStreamConsumer;
import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import com.food.ordering.system.payment.service.application.ports.in.PaymentRequestMessageListener;
import com.food.ordering.system.payment.service.adapter.messaging.mapper.PaymentMessagingDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentRequestKafkaListener implements KafkaStreamConsumer<PaymentRequestModel> {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestKafkaListener.class);

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    public void receive(List<PaymentRequestModel> messages) {
        // This method is required by the interface but not used
        // The actual processing is done by the @KafkaListener method
    }

    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
                topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestModel> messages,
                        @Header(value = KafkaHeaders.KEY, required = false) List<String> keys,
                        @Header(value = KafkaHeaders.PARTITION, required = false) List<Integer> partitions,
                        @Header(value = KafkaHeaders.OFFSET, required = false) List<Long> offsets) {
        log.info("{} number of payment requests received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys != null ? keys.toString() : "null",
                partitions != null ? partitions.toString() : "null",
                offsets != null ? offsets.toString() : "null");

        messages.forEach(paymentRequestModel -> {
            if (PaymentRequestModel.PaymentOrderStatus.PENDING == paymentRequestModel.getPaymentOrderStatus()) {
                log.info("Processing payment for order id: {}", paymentRequestModel.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                        .paymentRequestModelToPaymentRequest(paymentRequestModel));
            } else if(PaymentRequestModel.PaymentOrderStatus.CANCELLED == paymentRequestModel.getPaymentOrderStatus()) {
                log.info("Cancelling payment for order id: {}", paymentRequestModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                        .paymentRequestModelToPaymentRequest(paymentRequestModel));
            }
        });
    }
}
