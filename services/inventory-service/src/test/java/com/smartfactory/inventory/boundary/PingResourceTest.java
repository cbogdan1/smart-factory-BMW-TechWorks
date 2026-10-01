package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class PingResourceTest {

    @Test
    void shouldReturnPingStatus() {
        final PingResource pingResource = new PingResource();
        final Map<String, String> response = pingResource.ping();

        assertEquals("inventory-service", response.get("service"));
        assertEquals("UP", response.get("status"));
    }
}