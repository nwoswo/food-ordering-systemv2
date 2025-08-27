package com.food.ordering.system.order.service.infrastructure.order.repository;

import com.food.ordering.system.order.service.infrastructure.order.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("SELECT oe FROM OutboxEvent oe WHERE oe.processed = false ORDER BY oe.createdAt ASC")
    List<OutboxEvent> findUnprocessedEvents();

    @Query("SELECT oe FROM OutboxEvent oe WHERE oe.aggregateId = :aggregateId AND oe.processed = false ORDER BY oe.createdAt ASC")
    List<OutboxEvent> findUnprocessedEventsByAggregateId(@Param("aggregateId") UUID aggregateId);

    @Modifying
    @Query("UPDATE OutboxEvent oe SET oe.processed = true WHERE oe.id = :id")
    void markAsProcessed(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM OutboxEvent oe WHERE oe.processed = true AND oe.createdAt < :cutoffDate")
    void deleteProcessedEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
