package com.food.ordering.system.payment.service.infrastructure.persistence.repositories;

import com.food.ordering.system.payment.service.infrastructure.persistence.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderId(UUID orderId);
}
