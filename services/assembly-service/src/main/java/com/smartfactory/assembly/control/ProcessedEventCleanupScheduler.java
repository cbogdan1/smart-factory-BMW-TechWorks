package com.smartfactory.assembly.control;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@ApplicationScoped
public class ProcessedEventCleanupScheduler {
    @Inject
    EntityManager entityManager;

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    void cleanupProcessedEvents() {
        final int deletedRows = entityManager.createNativeQuery("""
                DELETE FROM event_processed
                WHERE processed_at < :cutoff
                """).setParameter("cutoff", OffsetDateTime.now().minusMonths(1)).executeUpdate();

        log.info("Deleted {} processed events older than 1 month", deletedRows);
    }
}
