package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NO_AVAILABLE_AGV;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NoAvailableAgvException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoAvailableAgvException() {
        super("No available AGV in the fleet", NO_AVAILABLE_AGV, 409);
    }
}
