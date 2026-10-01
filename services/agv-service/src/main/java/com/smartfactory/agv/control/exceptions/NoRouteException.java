package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NO_ROUTE_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NoRouteException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NoRouteException(final String sourceNodeId, final String targetNodeId) {

        super("No route available from " + sourceNodeId + " to " + targetNodeId, NO_ROUTE_FOUND, 422);
    }
}