package com.smartfactory.procurement.boundary;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.procurement.control.service.SimulateDeliveryService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SimulateDeliveryResourceTest {

    @InjectMock
    SimulateDeliveryService simulateDeliveryService;

    @InjectMock
    PartsDeliveredPublisher partsDeliveredPublisher;

    @Test
    void testSimulateDelivery_Returns200AndPublishesEvent() {

        final PartsDeliveredPayload payload =
                new PartsDeliveredPayload("PO-001", "SUP-001", "PART-BAT-001", "PART-BAT-001", 2, Instant.now());

        final DomainEvent<PartsDeliveredPayload> domainEvent =
                new DomainEvent<>("EVT-001", "parts-delivered", "procurement-service", "BMW-001", payload);

        final PartsDeliveredEvent event = new PartsDeliveredEvent(domainEvent);

        when(simulateDeliveryService.simulateDelivery("PO-001", "BMW-001")).thenReturn(event);

        doNothing().when(partsDeliveredPublisher).publish(any());

        given().contentType(ContentType.JSON).when().post("/api/v1/simulate-delivery/PO-001/deliver").then()
                .statusCode(200);

        verify(simulateDeliveryService).simulateDelivery("PO-001", "BMW-001");

        verify(partsDeliveredPublisher).publish(event);
    }

    @Test
    void testSimulateDelivery_NotFound_Returns404() {

        when(simulateDeliveryService.simulateDelivery("PO-INVALID", "BMW-001")).thenThrow(
                new BusinessException("Purchase Order id: PO-INVALID not found.",
                        ProcurementServiceExceptions.PURCHASE_ORDER_NOT_FOUND, 404));

        given().contentType(ContentType.JSON).when().post("/api/v1/simulate-delivery/PO-INVALID/deliver").then()
                .statusCode(404);
    }
}