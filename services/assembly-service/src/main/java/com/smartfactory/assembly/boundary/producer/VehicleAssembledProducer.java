package com.smartfactory.assembly.boundary.producer;

import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_MESSAGE;

import java.util.UUID;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class VehicleAssembledProducer {

    private static final String SOURCE_SYSTEM = "assembly-service";

    @Channel(VEHICLE_ASSEMBLED)
    Emitter<VehicleAssembledEvent> emitter;

    public void publishVehicleAssembled(
            final VehicleAssembledPayload payload) {
        if (payload == null) {
            throw new BusinessException(NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_MESSAGE, NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_CODE, 400);
        }

        final String eventId = "EVT-" + UUID.randomUUID();

        final DomainEvent<VehicleAssembledPayload> domainEvent =
                new DomainEvent<>(
                        eventId,
                        VEHICLE_ASSEMBLED,
                        DomainEvent.CURRENT_VERSION,
                        payload.assembledAt(),
                        SOURCE_SYSTEM,
                        payload.vehicleId(),
                        payload);

        final VehicleAssembledEvent event =
                new VehicleAssembledEvent(domainEvent);

        final OutgoingKafkaRecordMetadata<String> metadata =
                OutgoingKafkaRecordMetadata.<String>builder()
                        .withKey(domainEvent.kafkaKey())
                        .build();

        final Message<VehicleAssembledEvent> message =
                Message.of(event)
                        .addMetadata(metadata);

        log.info(
                "Publishing vehicle-assembled event: eventId={}, vehicleId={}",
                eventId,
                payload.vehicleId());

        emitter.send(message);
    }
}
