package com.food.ordering.system.restaurant.service.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.food.ordering.system.restaurant.service.infrastructure.persistence", "com.food.ordering.system.common.dataaccess" })
@EntityScan(basePackages = { "com.food.ordering.system.restaurant.service.infrastructure.persistence", "com.food.ordering.system.common.dataaccess" })
@SpringBootApplication(scanBasePackages = { "com.food.ordering.system", "com.food.ordering.system.restaurant.service.application" })
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
