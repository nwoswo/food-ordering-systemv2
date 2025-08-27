package com.food.ordering.system.order.service.infrastructure.order.adapter;

import com.food.ordering.system.order.service.infrastructure.order.entity.OutboxEvent;
import com.food.ordering.system.order.service.infrastructure.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void saveEvent(UUID aggregateId, String aggregateType, String eventType, String eventData) {
        OutboxEvent event = OutboxEvent.of(aggregateId, aggregateType, eventType, eventData);
        outboxEventRepository.save(event);
        log.info("Event saved to outbox: {} for aggregate: {}", eventType, aggregateId);
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> getUnprocessedEvents() {
        return outboxEventRepository.findUnprocessedEvents();
    }

    @Transactional
    public void markEventAsProcessed(UUID eventId) {
        outboxEventRepository.markAsProcessed(eventId);
        log.debug("Event marked as processed: {}", eventId);
    }

    @Transactional
    public void cleanupProcessedEvents() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // Keep 7 days
        outboxEventRepository.deleteProcessedEventsOlderThan(cutoffDate);
        log.info("Cleaned up processed events older than: {}", cutoffDate);
    }
}
