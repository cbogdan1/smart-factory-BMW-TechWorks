package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.INVALID_NODE_TYPE;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class InvalidNodeTypeException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidNodeTypeException() {
        super("Invalid Type. Accepted values are: WAREHOUSE, BUFFER, JUNCTION, PRODUCTION_LINE, QUALITY_AREA, LINE_STATION",
                INVALID_NODE_TYPE, 400);
    }
}