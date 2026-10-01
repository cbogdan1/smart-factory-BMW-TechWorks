package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.SOURCE_NODE_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class SourceNodeNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SourceNodeNotFoundException(final String nodeId) {
        super("Source node not found: " + nodeId, SOURCE_NODE_NOT_FOUND, 404);
    }
}
