package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.stream.consumer.KafkaStreamConsumer;
import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentResponseKafkaListener implements KafkaStreamConsumer<PaymentResponseModel> {

    private static final Logger log = LoggerFactory.getLogger(PaymentResponseKafkaListener.class);
    
    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                        OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    public void receive(List<PaymentResponseModel> messages) {
        // This method is required by the interface but not used in practice
        // The actual processing is done by the @KafkaListener method below
    }

    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", 
                   topics = "${order-service.payment-response-topic-name}",
                   containerFactory = "paymentResponseKafkaListenerContainerFactory")
    public void receive(@Payload List<PaymentResponseModel> messages,
                        @Header(value = KafkaHeaders.KEY, required = false) List<String> keys,
                        @Header(value = KafkaHeaders.PARTITION, required = false) List<Integer> partitions,
                        @Header(value = KafkaHeaders.OFFSET, required = false) List<Long> offsets) {
        log.info("{} number of payment responses received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys != null ? keys.toString() : "null",
                partitions != null ? partitions.toString() : "null",
                offsets != null ? offsets.toString() : "null");

        messages.forEach(paymentResponseModel -> {
            if (PaymentResponseModel.PaymentStatus.COMPLETED == paymentResponseModel.getPaymentStatus()) {
                log.info("Processing successful payment for order id: {}", paymentResponseModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper
                        .paymentResponseModelToPaymentResponse(paymentResponseModel));
            } else if (PaymentResponseModel.PaymentStatus.CANCELLED == paymentResponseModel.getPaymentStatus() ||
                    PaymentResponseModel.PaymentStatus.FAILED == paymentResponseModel.getPaymentStatus()) {
                log.info("Processing unsuccessful payment for order id: {}", paymentResponseModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper
                        .paymentResponseModelToPaymentResponse(paymentResponseModel));
            }
        });
    }
}
