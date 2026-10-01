package com.smartfactory.procurement.control.service;

import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;

public interface ProcurementService {
    public PartsDeliveredEvent createPurchaseOrder(final PartsShortageDetectedEvent event);
}
