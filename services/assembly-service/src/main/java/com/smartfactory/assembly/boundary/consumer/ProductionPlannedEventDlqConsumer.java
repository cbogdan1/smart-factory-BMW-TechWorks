package com.smartfactory.assembly.boundary.consumer;

import static com.smartfactory.common.Topics.PRODUCTION_PLANNED_DLQ;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class ProductionPlannedEventDlqConsumer {
    @Incoming(PRODUCTION_PLANNED_DLQ)
    public CompletionStage<Void> consume(final Message<String> message) {
        log.error("DLQ message received: {}", message.getPayload());
        return message.ack();
    }
}