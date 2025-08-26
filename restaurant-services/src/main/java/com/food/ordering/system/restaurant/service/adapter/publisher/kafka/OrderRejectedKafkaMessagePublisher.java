package com.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseModel;
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;

@Component
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMessagePublisher {

  private static final Logger log = LoggerFactory.getLogger(OrderRejectedKafkaMessagePublisher.class);

  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalResponseModel> kafkaProducer;
  private final RestaurantServiceConfigData restaurantServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  public OrderRejectedKafkaMessagePublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
      KafkaProducer<String, RestaurantApprovalResponseModel> kafkaProducer,
      RestaurantServiceConfigData restaurantServiceConfigData,
      KafkaMessageHelper kafkaMessageHelper) {
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.restaurantServiceConfigData = restaurantServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
  }

  @Override
  public void publish(OrderRejectedEvent orderRejectedEvent) {
    String orderId = orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString();

    log.info("Received OrderRejectedEvent for order id: {}", orderId);

    try {
      RestaurantApprovalResponseModel restaurantApprovalResponseModel = restaurantMessagingDataMapper
          .orderRejectedEventToRestaurantApprovalResponseModel(orderRejectedEvent);

      kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
          orderId,
          restaurantApprovalResponseModel,
          kafkaMessageHelper);

      log.info("RestaurantApprovalResponseModel sent to kafka at: {}", System.nanoTime());
    } catch (Exception e) {
      log.error("Error while sending RestaurantApprovalResponseModel message" +
          " to kafka with order id: {}, error: {}", orderId, e.getMessage());
    }
  }

}
