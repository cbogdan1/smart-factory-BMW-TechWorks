package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.MATERIAL_DEMANDS_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.MATERIAL_DEMANDS_NOT_FOUND_ERROR_MESSAGE;
import com.smartfactory.common.exception.BusinessException;

public class MaterialDemandsNotFoundException extends BusinessException {
    public MaterialDemandsNotFoundException(final String eventId) {
        super(
                MATERIAL_DEMANDS_NOT_FOUND_ERROR_MESSAGE + eventId,
                MATERIAL_DEMANDS_NOT_FOUND_ERROR_CODE,
                404
        );
    }
}
