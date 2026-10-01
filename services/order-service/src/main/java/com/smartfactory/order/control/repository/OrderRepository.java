package com.smartfactory.order.control.repository;

import java.util.Optional;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.order.entity.Order;
import com.smartfactory.order.entity.ProcessedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public Optional<Order> findByOrderId(final String orderId) {
        return find("orderId", orderId).firstResultOptional();
    }

    public void updateStatus(final String orderId, final OrderStatus newStatus) {
        update("status = ?1 where orderId = ?2", newStatus, orderId);
    }

    public Optional<Order> findByVehicleId(final String vehicleId) {
        return find("vehicleId", vehicleId).firstResultOptional();
    }

    public boolean isEventProcessed(final String eventId) {
        return ProcessedEvent.findById(eventId) != null;
    }

    public void markEventProcessed(final String eventId) {
        final ProcessedEvent processedEvent = new ProcessedEvent(eventId);
        processedEvent.persist();
    }

    public void update(final Order order) {
        this.persist(order);
    }
}