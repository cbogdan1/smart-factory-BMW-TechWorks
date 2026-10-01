package com.smartfactory.planning.boundary.kafka;

import java.util.concurrent.CompletionStage;

import com.smartfactory.common.Topics;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
@Slf4j
public class VehicleAssembledDlqConsumer {
    @Incoming(Topics.VEHICLE_ASSEMBLED_DLQ)
    public CompletionStage<Void> consume(final Message<String> message) {
        log.error("Vehicle assembled message moved to DLQ: {}", message.getPayload());
        return message.ack();
    }
}
