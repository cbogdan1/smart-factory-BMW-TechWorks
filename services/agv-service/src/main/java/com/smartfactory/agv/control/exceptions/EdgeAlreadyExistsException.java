package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.EDGE_ALREADY_EXISTS;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class EdgeAlreadyExistsException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EdgeAlreadyExistsException(final String edgeId) {
        super("Edge ID already exists: " + edgeId, EDGE_ALREADY_EXISTS, 409);
    }
}