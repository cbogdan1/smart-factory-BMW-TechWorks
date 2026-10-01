package com.smartfactory.quality.boundary.consumers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.quality.control.services.InspectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VehicleAssembledConsumerTest {

    @Mock
    InspectionService inspectionService;

    @InjectMocks
    VehicleAssembledConsumer vehicleAssembledConsumer;

    @Test
    public void delegatesToInspectionService() {
        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEH-001", "ORD-001", "MODEL-X", "LINE-1", Instant.now());
        final VehicleAssembledEvent event = new VehicleAssembledEvent(
                new DomainEvent<>("EVT-1", Topics.VEHICLE_ASSEMBLED, "assembly-service", "VEH-001", payload));

        vehicleAssembledConsumer.consume(event);

        verify(inspectionService).performInspection("EVT-1", payload);
    }

    @Test
    public void rejectsNullEvent() {
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> vehicleAssembledConsumer.consume(null));

        assertEquals("INVALID_VEHICLE_ASSEMBLED_EVENT", exception.getErrorCode());
        verifyNoInteractions(inspectionService);
    }
}
