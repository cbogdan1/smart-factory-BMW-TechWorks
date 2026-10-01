package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.AGV_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class AgvNotFoundException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AgvNotFoundException(final String agvId) {
        super("Agv not found: " + agvId, AGV_NOT_FOUND, 404);
    }
}
