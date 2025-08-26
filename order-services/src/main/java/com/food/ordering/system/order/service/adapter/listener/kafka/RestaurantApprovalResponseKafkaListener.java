package com.food.ordering.system.order.service.messaging.listener.kafka;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.food.ordering.system.kafka.stream.consumer.KafkaStreamConsumer;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaStreamConsumer<RestaurantApprovalResponseModel> {

  private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalResponseKafkaListener.class);
  private static final String FAILURE_MESSAGE_DELIMITER = ",";

  private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
  private final OrderMessagingDataMapper orderMessagingDataMapper;

  public RestaurantApprovalResponseKafkaListener(
      RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener,
      OrderMessagingDataMapper orderMessagingDataMapper) {
    this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
  }

  @Override
  public void receive(List<RestaurantApprovalResponseModel> messages) {
    // This method is required by the interface but not used in practice
    // The actual processing is done by the @KafkaListener method below
  }

  @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}", 
                 topics = "${order-service.restaurant-approval-response-topic-name}",
                 containerFactory = "restaurantApprovalResponseKafkaListenerContainerFactory")
  public void receive(@Payload List<RestaurantApprovalResponseModel> messages,
      @Header(value = KafkaHeaders.KEY, required = false) List<String> keys,
      @Header(value = KafkaHeaders.PARTITION, required = false) List<Integer> partitions,
      @Header(value = KafkaHeaders.OFFSET, required = false) List<Long> offsets) {
    log.info("{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
        messages.size(),
        keys != null ? keys.toString() : "null",
        partitions != null ? partitions.toString() : "null",
        offsets != null ? offsets.toString() : "null");

    messages.forEach(restaurantApprovalResponseModel -> {
      if (RestaurantApprovalResponseModel.OrderApprovalStatus.APPROVED == restaurantApprovalResponseModel
          .getOrderApprovalStatus()) {
        log.info("Processing approved order for order id: {}",
            restaurantApprovalResponseModel.getOrderId());
        restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper
            .approvalResponseModelToApprovalResponse(restaurantApprovalResponseModel));
      } else if (RestaurantApprovalResponseModel.OrderApprovalStatus.REJECTED == restaurantApprovalResponseModel
          .getOrderApprovalStatus()) {
        log.info("Processing rejected order for order id: {}, with failure messages: {}",
            restaurantApprovalResponseModel.getOrderId(),
            String.join(FAILURE_MESSAGE_DELIMITER,
                restaurantApprovalResponseModel.getFailureMessages()));
        restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
            .approvalResponseModelToApprovalResponse(restaurantApprovalResponseModel));
      }
    });

  }
}
