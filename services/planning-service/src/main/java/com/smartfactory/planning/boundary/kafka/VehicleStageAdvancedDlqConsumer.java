package com.smartfactory.planning.boundary.kafka;

import java.util.concurrent.CompletionStage;

import com.smartfactory.common.Topics;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
@Slf4j
public class VehicleStageAdvancedDlqConsumer {
    @Incoming(Topics.VEHICLE_STAGE_ADVANCED_DLQ)
    public CompletionStage<Void> consume(final Message<String> message) {
        log.error("Vehicle stage advanced message moved to DLQ: {}", message.getPayload());
        return message.ack();
    }
}