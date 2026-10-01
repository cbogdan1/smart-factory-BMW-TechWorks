package com.smartfactory.order.control.order_service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Generates business identifiers for orders and vehicles.
 */
@ApplicationScoped
public class IdGeneratorService {

    public String generateOrderId() {
        return "ORD-" + UUID.randomUUID();
    }

    public String generateVehicleId() {
        return "VEH-" + UUID.randomUUID();
    }

}