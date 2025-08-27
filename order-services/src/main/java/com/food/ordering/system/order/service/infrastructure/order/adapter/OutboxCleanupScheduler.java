package com.food.ordering.system.order.service.infrastructure.order.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxCleanupScheduler {

    private final OutboxService outboxService;

    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
    public void cleanupProcessedEvents() {
        try {
            log.info("Starting outbox cleanup process...");
            outboxService.cleanupProcessedEvents();
            log.info("Outbox cleanup completed successfully");
        } catch (Exception e) {
            log.error("Failed to cleanup outbox events", e);
        }
    }
}
