package com.smartfactory.procurement.boundary;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.PartsDeliveredEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PartsDeliveredPublisher {

    private static final Logger LOG = Logger.getLogger(PartsDeliveredPublisher.class);

    @Inject
    @Channel(Topics.PARTS_DELIVERED)
    Emitter<PartsDeliveredEvent> emitter;

    public void publish(final PartsDeliveredEvent event) {
        emitter.send(event).whenComplete((success, failure) -> {
            if (failure != null) {
                LOG.errorf(failure, "Failed to publish event %s to topic %s", event.event().eventId(),
                        Topics.PARTS_DELIVERED);
            }
        });
    }
}