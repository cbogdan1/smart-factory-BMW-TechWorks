package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.TARGET_NODE_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class TargetNodeNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TargetNodeNotFoundException(final String nodeId) {
        super("Target node not found: " + nodeId, TARGET_NODE_NOT_FOUND, 404);
    }
}
