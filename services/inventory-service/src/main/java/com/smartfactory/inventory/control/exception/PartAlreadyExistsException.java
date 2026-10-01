package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.PART_ALREADY_EXISTS_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.PART_ALREADY_EXISTS_ERROR_MESSAGE;
import com.smartfactory.common.exception.BusinessException;

public class PartAlreadyExistsException extends BusinessException {
    public PartAlreadyExistsException(final String partCode) {
        super(
                String.format(PART_ALREADY_EXISTS_ERROR_MESSAGE, partCode),
                PART_ALREADY_EXISTS_ERROR_CODE,
                409
        );
    }
}
