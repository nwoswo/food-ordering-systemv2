package com.food.ordering.system.payment.service.adapter.outbound.messaging.kafka;

import com.food.ordering.system.kafka.stream.model.PaymentResponseModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import com.food.ordering.system.payment.service.infrastructure.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.model.events.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.application.ports.out.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.adapter.messaging.mapper.PaymentMessagingDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentCancelledKafkaMessagePublisher implements PaymentCancelledMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentCancelledKafkaMessagePublisher.class);

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentCancelledKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                                 KafkaProducer<String, PaymentResponseModel> kafkaProducer,
                                                 PaymentServiceConfigData paymentServiceConfigData,
                                                 KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(PaymentCancelledEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();

        log.info("Received PaymentCancelledEvent for order id: {}", orderId);

        try {
            PaymentResponseModel paymentResponseModel =
                    paymentMessagingDataMapper.paymentCancelledEventToPaymentResponseModel(domainEvent);

            kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseModel,
                    kafkaMessageHelper);

            log.info("PaymentResponseModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
