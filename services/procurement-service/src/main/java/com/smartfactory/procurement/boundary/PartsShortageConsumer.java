package com.smartfactory.procurement.boundary;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.procurement.control.service.ProcurementService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
@ActivateRequestContext
public class PartsShortageConsumer {
    @Inject
    PartsDeliveredPublisher partsDeliveredPublisher;
    @Inject
    ProcurementService procurementService;

    @Incoming(Topics.PARTS_SHORTAGE_DETECTED)
    @Blocking
    public void onShortage(final PartsShortageDetectedEvent event) {
        if (event == null || event.event() == null) {
            throw new BusinessException("Null event");
        }
        final PartsDeliveredEvent partsDeliveredEvent = procurementService.createPurchaseOrder(event);
        if (partsDeliveredEvent != null) {
            partsDeliveredPublisher.publish(partsDeliveredEvent);
        }
    }
}