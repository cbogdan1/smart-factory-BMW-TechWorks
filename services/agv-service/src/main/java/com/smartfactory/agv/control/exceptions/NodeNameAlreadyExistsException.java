package com.smartfactory.agv.control.exceptions;

import static com.smartfactory.common.exception.AgvServiceExceptions.NODE_NAME_ALREADY_EXISTS;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class NodeNameAlreadyExistsException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NodeNameAlreadyExistsException(final String name) {
        super("Name already exists: " + name, NODE_NAME_ALREADY_EXISTS, 409);
    }
}
