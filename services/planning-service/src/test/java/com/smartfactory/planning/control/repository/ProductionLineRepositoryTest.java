package com.smartfactory.planning.control.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartfactory.planning.entity.ProductionLine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductionLineRepositoryTest {
    @Inject
    ProductionLineRepository repository;

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    void incrementLoad_shouldIncreaseCurrentLoadByOne() {
        final ProductionLine line = new ProductionLine();
        line.setLineId("TEST-LINE-INCREMENT");
        line.setName("Test Line");
        line.setMaximumCapacity(10);
        line.setCurrentLoad(3);
        line.setEnabled(true);

        entityManager.persist(line);
        entityManager.flush();

        repository.incrementLoad(line);

        assertEquals(4, line.getCurrentLoad());
    }

    @Test
    @Transactional
    void decrementLoad_shouldDecreaseCurrentLoadByOne() {
        final ProductionLine line = new ProductionLine();
        line.setLineId("TEST-LINE-DECREMENT");
        line.setName("Test Line");
        line.setMaximumCapacity(10);
        line.setCurrentLoad(3);
        line.setEnabled(true);

        entityManager.persist(line);
        entityManager.flush();

        repository.decrementLoad(line);

        assertEquals(2, line.getCurrentLoad());
    }

    @Test
    @Transactional
    void decrementLoad_shouldNotGoBelowZero() {
        final ProductionLine line = new ProductionLine();
        line.setLineId("TEST-LINE-ZERO");
        line.setName("Test Line");
        line.setMaximumCapacity(10);
        line.setCurrentLoad(0);
        line.setEnabled(true);

        entityManager.persist(line);
        entityManager.flush();

        repository.decrementLoad(line);

        assertEquals(0, line.getCurrentLoad());
    }

    @Test
    @Transactional
    void findByLineIdForUpdate_shouldReturnProductionLine() {
        final ProductionLine line = new ProductionLine();
        line.setLineId("TEST-LINE-FIND");
        line.setName("Test Line");
        line.setMaximumCapacity(10);
        line.setCurrentLoad(5);
        line.setEnabled(true);

        entityManager.persist(line);
        entityManager.flush();

        final var result = repository.findByLineIdForUpdate("TEST-LINE-FIND");

        assertTrue(result.isPresent());
        assertEquals("TEST-LINE-FIND", result.get().getLineId());
        assertEquals(5, result.get().getCurrentLoad());
    }

    @Test
    @Transactional
    void findByLineIdForUpdate_shouldReturnEmptyForUnknownLine() {
        final var result = repository.findByLineIdForUpdate("DOES-NOT-EXIST");

        assertTrue(result.isEmpty());
    }
}