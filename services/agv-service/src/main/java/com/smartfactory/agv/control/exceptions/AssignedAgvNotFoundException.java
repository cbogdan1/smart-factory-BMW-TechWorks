package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.ASSIGNED_AGV_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class AssignedAgvNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public AssignedAgvNotFoundException(final String agvId) {
        super("Assigned AGV not found: " + agvId, ASSIGNED_AGV_NOT_FOUND, 404);
    }
}