package com.smartfactory.inventory.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.smartfactory.inventory.control.mapper.MaterialDemandMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.inventory.entity.MaterialDemand;
import com.smartfactory.inventory.entity.VehicleBom;

class MaterialDemandServiceTest {

    @InjectMocks
    MaterialDemandService materialDemandService;

    @Mock
    MaterialDemandRepository materialDemandRepository;

    @Mock
    VehicleBomRepository vehicleBomRepository;

    @Mock
    ProductionPlannedPayload payload;

    @Mock
    VehicleBom bomEntry1;

    @Mock
    VehicleBom bomEntry2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnFalseWhenEventAlreadyExists() {

        String eventId = "EVT-123";

        when(materialDemandRepository.existsByEventId(eventId))
                .thenReturn(true);

        boolean result =
                materialDemandService.recordDemand(payload, eventId);

        assertFalse(result);

        verify(materialDemandRepository)
                .existsByEventId(eventId);

        verifyNoInteractions(vehicleBomRepository);

        verify(materialDemandRepository, never())
                .persist(any(MaterialDemand.class));
    }

    @Test
    void shouldReturnFalseWhenVehicleBomDoesNotExist() {

        String eventId = "EVT-123";

        when(materialDemandRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(payload.vehicleModel())
                .thenReturn("MODEL-X");

        when(vehicleBomRepository.findByVehicleModel("MODEL-X"))
                .thenReturn(List.of());

        boolean result =
                materialDemandService.recordDemand(payload, eventId);

        assertFalse(result);

        verify(vehicleBomRepository)
                .findByVehicleModel("MODEL-X");

        verify(materialDemandRepository, never())
                .persist(any(MaterialDemand.class));
    }

    @Test
    void shouldCreateOneDemandForEachBomEntry() {

        String eventId = "EVT-123";

        when(materialDemandRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(payload.vehicleModel())
                .thenReturn("MODEL-X");

        when(vehicleBomRepository.findByVehicleModel("MODEL-X"))
                .thenReturn(List.of(bomEntry1, bomEntry2));

        MaterialDemand demand1 = new MaterialDemand();
        MaterialDemand demand2 = new MaterialDemand();

        try (var mockedMapper =
                mockStatic(MaterialDemandMapper.class)) {

            mockedMapper.when(() ->
                    MaterialDemandMapper.toEntity(
                            payload,
                            eventId,
                            bomEntry1
                    )
            ).thenReturn(demand1);

            mockedMapper.when(() ->
                    MaterialDemandMapper.toEntity(
                            payload,
                            eventId,
                            bomEntry2
                    )
            ).thenReturn(demand2);

            boolean result =
                    materialDemandService.recordDemand(payload, eventId);

            assertTrue(result);

            verify(materialDemandRepository)
                    .persist(demand1);

            verify(materialDemandRepository)
                    .persist(demand2);

            verify(materialDemandRepository, times(2))
                    .persist(any(MaterialDemand.class));
        }
    }

    @Test
    void shouldUsePayloadVehicleModelToFindBom() {

        String eventId = "EVT-123";

        when(materialDemandRepository.existsByEventId(eventId))
                .thenReturn(false);

        when(payload.vehicleModel())
                .thenReturn("BMW-X5");

        when(vehicleBomRepository.findByVehicleModel("BMW-X5"))
                .thenReturn(List.of());

        materialDemandService.recordDemand(payload, eventId);

        verify(vehicleBomRepository)
                .findByVehicleModel("BMW-X5");
    }
}
