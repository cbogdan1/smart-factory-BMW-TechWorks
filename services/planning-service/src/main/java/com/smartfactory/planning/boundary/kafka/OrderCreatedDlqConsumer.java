package com.smartfactory.planning.boundary.kafka;

import java.util.concurrent.CompletionStage;

import com.smartfactory.common.Topics;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class OrderCreatedDlqConsumer {
    @Incoming(Topics.ORDER_CREATED_DLQ)
    public CompletionStage<Void> consume(final Message<String> message) {
        log.error("DLQ message received: {}", message.getPayload());
        return message.ack();
    }
}