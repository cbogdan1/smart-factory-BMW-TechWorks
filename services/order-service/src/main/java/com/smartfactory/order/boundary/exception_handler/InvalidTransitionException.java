package com.smartfactory.order.boundary.exception_handler;

import static com.smartfactory.common.exception.OrderErrorCodes.INVALID_STATUS_TRANSITION;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class InvalidTransitionException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 7218038690533859388L;

    public InvalidTransitionException(final String message) {
        super(message, INVALID_STATUS_TRANSITION, 409);
    }
}
