package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.INVENTORY_STOCK_NOT_FOUND_ERROR_MESSAGE;
import java.util.UUID;
import com.smartfactory.common.exception.BusinessException;

public class InventoryStockNotFoundException extends BusinessException {
    public InventoryStockNotFoundException(final String partId) {
        super(
                INVENTORY_STOCK_NOT_FOUND_ERROR_MESSAGE + partId,
                INVENTORY_STOCK_NOT_FOUND_ERROR_CODE,
                404
        );
    }

    public InventoryStockNotFoundException(final UUID partId) {
        super(
                INVENTORY_STOCK_NOT_FOUND_ERROR_MESSAGE + partId,
                INVENTORY_STOCK_NOT_FOUND_ERROR_CODE,
                404
        );
    }
}
