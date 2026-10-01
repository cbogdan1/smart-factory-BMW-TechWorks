package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.INSUFFICIENT_STOCK_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INSUFFICIENT_STOCK_ERROR_MESSAGE;
import com.smartfactory.common.exception.BusinessException;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(final String eventId) {
        super(
                INSUFFICIENT_STOCK_ERROR_MESSAGE + eventId,
                INSUFFICIENT_STOCK_ERROR_CODE,
                404
        );
    }
}
