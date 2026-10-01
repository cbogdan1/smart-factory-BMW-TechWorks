package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.EDGE_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class EdgeNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EdgeNotFoundException(final String edgeId) {
        super("Edge not found: " + edgeId, EDGE_NOT_FOUND, 404);
    }
}
