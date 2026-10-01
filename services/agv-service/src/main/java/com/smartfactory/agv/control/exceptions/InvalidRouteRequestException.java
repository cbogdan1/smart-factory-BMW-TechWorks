package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.INVALID_ROUTE_REQUEST;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class InvalidRouteRequestException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidRouteRequestException() {

        super("Source and target nodes are required", INVALID_ROUTE_REQUEST, 400);
    }
}