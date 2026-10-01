package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.PART_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.PART_WITH_CODE_NOT_FOUND_ERROR_MESSAGE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.PART_WITH_ID_NOT_FOUND_ERROR_MESSAGE;
import java.util.UUID;
import com.smartfactory.common.exception.BusinessException;

public class PartNotFoundException extends BusinessException {
    public PartNotFoundException(final UUID partId) {
        super(
                String.format(PART_WITH_ID_NOT_FOUND_ERROR_MESSAGE, partId),
                PART_NOT_FOUND_ERROR_CODE,
                404
        );
    }

    public PartNotFoundException(final String partCode) {
        super(
                String.format(PART_WITH_CODE_NOT_FOUND_ERROR_MESSAGE, partCode),
                PART_NOT_FOUND_ERROR_CODE,
                404
        );
    }
}
