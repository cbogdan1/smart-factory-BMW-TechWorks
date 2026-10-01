package com.smartfactory.inventory.control.exception;

import static com.smartfactory.common.exception.InventoryServiceExceptions.RESERVATION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.InventoryServiceExceptions.RESERVATION_NOT_FOUND_ERROR_MESSAGE;
import com.smartfactory.common.exception.BusinessException;

public class ReservationNotFoundException extends BusinessException {
    public ReservationNotFoundException(final String demandId) {
        super(
                RESERVATION_NOT_FOUND_ERROR_MESSAGE + demandId,
                RESERVATION_NOT_FOUND_ERROR_CODE,
                404
        );
    }
}
