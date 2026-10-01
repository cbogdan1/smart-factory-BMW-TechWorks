package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NODE_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NodeNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NodeNotFoundException(final String nodeId) {
        super("Node not found: " + nodeId, NODE_NOT_FOUND, 404);
    }
}
