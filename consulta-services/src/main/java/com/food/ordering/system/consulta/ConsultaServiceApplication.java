package com.food.ordering.system.consulta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.food.ordering.system.consulta.dataaccess" })
@EntityScan(basePackages = { "com.food.ordering.system.consulta.dataaccess" })
@SpringBootApplication(scanBasePackages = { "com.food.ordering.system", "com.food.ordering.system.consulta" })
public class ConsultaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultaServiceApplication.class, args);
    }
}
