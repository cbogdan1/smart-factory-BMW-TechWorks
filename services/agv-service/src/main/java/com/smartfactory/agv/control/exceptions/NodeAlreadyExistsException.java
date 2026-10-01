package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NODE_ALREADY_EXISTS;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NodeAlreadyExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NodeAlreadyExistsException(final String nodeId) {
        super("Node ID already exists: " + nodeId, NODE_ALREADY_EXISTS, 409);
    }
}
