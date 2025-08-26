package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.OrderApplicationServiceImpl;
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
