package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.PRODUCTION_PLANNED_EVENT_NULL_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.PRODUCTION_PLANNED_EVENT_NULL_ERROR_MESSAGE;

import com.smartfactory.common.exception.BusinessException;

public class ProductionPlannedEventEmptyException extends BusinessException {
    public ProductionPlannedEventEmptyException() {
        super(
                PRODUCTION_PLANNED_EVENT_NULL_ERROR_MESSAGE,
                PRODUCTION_PLANNED_EVENT_NULL_ERROR_CODE,
                400
        );
    }
}
