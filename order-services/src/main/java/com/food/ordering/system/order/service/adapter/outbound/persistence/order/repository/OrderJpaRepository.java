package com.food.ordering.system.order.service.dataaccess.order.repository;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByTrackingId(UUID trackingId);

    @Query("SELECT o FROM OrderEntity o " +
           "LEFT JOIN FETCH o.items " +
           "LEFT JOIN FETCH o.address " +
           "WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithItemsAndAddress(@Param("orderId") UUID orderId);

    @Query("SELECT o FROM OrderEntity o " +
           "LEFT JOIN FETCH o.items " +
           "LEFT JOIN FETCH o.address " +
           "WHERE o.trackingId = :trackingId")
    Optional<OrderEntity> findByTrackingIdWithItemsAndAddress(@Param("trackingId") UUID trackingId);
}
