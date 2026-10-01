package com.smartfactory.inventory.control.exception;

import com.smartfactory.common.exception.BusinessException;

public class InventoryStockInvalidQuantitiesException extends BusinessException {
    public InventoryStockInvalidQuantitiesException(final String errorMessage, final String errorCode){
        super(
                errorMessage,
                errorCode,
                400
        );
    }

}
