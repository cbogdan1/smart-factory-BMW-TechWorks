package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NO_ASSIGNABLE_AGV;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NoAssignableAgvException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NoAssignableAgvException() {

        super("No route available from any available AGV to the target node", NO_ASSIGNABLE_AGV, 422);
    }
}
