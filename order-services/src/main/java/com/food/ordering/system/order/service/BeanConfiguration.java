package com.food.ordering.system.order.service;

import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.application.domain.OrderApplicationServiceImpl;
import com.food.ordering.system.order.service.application.domain.OrderCreateCommandHandler;
import com.food.ordering.system.order.service.application.domain.OrderTrackCommandHandler;
import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    public OrderApplicationService orderApplicationService(OrderCreateCommandHandler orderCreateCommandHandler,
                                                          OrderTrackCommandHandler orderTrackCommandHandler) {
        return new OrderApplicationServiceImpl(orderCreateCommandHandler, orderTrackCommandHandler);
    }
}
