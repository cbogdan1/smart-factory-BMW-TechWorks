package com.smartfactory.procurement.control.service;

import com.smartfactory.common.event.PartsDeliveredEvent;

public interface SimulateDeliveryService {

    PartsDeliveredEvent simulateDelivery(final String purchaseOrderId, final String vehicleId);
}
