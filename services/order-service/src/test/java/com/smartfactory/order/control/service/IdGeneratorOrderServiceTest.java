package com.smartfactory.order.control.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartfactory.order.control.order_service.IdGeneratorService;
import org.junit.jupiter.api.Test;

class IdGeneratorOrderServiceTest {

    private final IdGeneratorService service = new IdGeneratorService();

    @Test
    void shouldGenerateOrderId() {

        final String id = service.generateOrderId();

        assertNotNull(id);
        assertTrue(id.startsWith("ORD-"));
    }

    @Test
    void shouldGenerateVehicleId() {

        final String id = service.generateVehicleId();

        assertNotNull(id);
        assertTrue(id.startsWith("VEH-"));
    }
}