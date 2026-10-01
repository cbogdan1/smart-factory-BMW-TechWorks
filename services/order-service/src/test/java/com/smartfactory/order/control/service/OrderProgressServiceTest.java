package com.smartfactory.order.control.service;

import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.OrderErrorCodes.ORDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.dto.order.OrderProgressView;
import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.order.control.order_service.AssemblyClient;
import com.smartfactory.order.control.order_service.OrderProgressService;
import com.smartfactory.order.control.repository.OrderRepository;
import com.smartfactory.order.entity.Order;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderProgressServiceTest {

    @Mock
    OrderRepository repo;

    @Mock
    AssemblyClient assemblyClient;

    @InjectMocks
    OrderProgressService service;

    private Order order;

    @BeforeEach
    void setUp() {

        order = new Order();
        order.setOrderId("ORD-001");
        order.setVehicleId("VEH-001");
        order.setStatus(OrderStatus.IN_PRODUCTION);
        order.setBatteryType(BatteryType.LONG_RANGE);
        order.setPriority(Priority.HIGH);
        order.setCreatedDate(LocalDateTime.now());
        order.setLastModifiedDate(LocalDateTime.now());
    }

    @Test
    void shouldReturnOrderProgressView() {

        when(repo.findByOrderId("ORD-001")).thenReturn(Optional.of(order));

        when(assemblyClient.findByVehicleId("VEH-001")).thenReturn(
                new VehicleProductionView("VEH-001", "ORD-001", ProductionStage.PAINT, null, null, null));

        final OrderProgressView result = service.getProgressView("ORD-001");

        assertEquals("ORD-001", result.orderId());
        assertEquals("VEH-001", result.vehicleId());
        assertEquals(OrderStatus.IN_PRODUCTION, result.status());
        assertEquals(ProductionStage.PAINT, result.currentFactoryStage());
        assertEquals(order.getLastModifiedDate(), result.lastUpdated());
        assertEquals(order.getCompletionDate(), result.completionDate());
    }

    @Test
    void shouldThrowOrderNotFoundException() {
        when(repo.findByOrderId("ORD-001")).thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.getProgressView("ORD-001"));
        assertEquals(ORDER_NOT_FOUND, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void shouldThrowVehicleProductionNotFoundException() {

        when(repo.findByOrderId("ORD-001")).thenReturn(Optional.of(order));

        when(assemblyClient.findByVehicleId("VEH-001")).thenThrow(
                new WebApplicationException(Response.status(404).build()));
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> service.getProgressView("ORD-001")

                );
        assertEquals(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
    }
}
