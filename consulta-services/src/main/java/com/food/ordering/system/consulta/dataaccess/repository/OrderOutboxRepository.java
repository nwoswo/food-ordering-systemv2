package com.food.ordering.system.consulta.dataaccess.repository;

import com.food.ordering.system.consulta.dataaccess.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutboxEntity, UUID> {
    
    Optional<OrderOutboxEntity> findByOrderId(UUID orderId);
    
    List<OrderOutboxEntity> findByAggregateId(UUID aggregateId);
    
    List<OrderOutboxEntity> findByEventType(String eventType);
    
    List<OrderOutboxEntity> findByProcessed(boolean processed);
}
