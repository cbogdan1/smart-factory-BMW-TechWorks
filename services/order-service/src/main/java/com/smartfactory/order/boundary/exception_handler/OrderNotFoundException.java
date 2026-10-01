package com.smartfactory.order.boundary.exception_handler;

import static com.smartfactory.common.exception.OrderErrorCodes.ORDER_NOT_FOUND;

import java.io.Serial;

import com.smartfactory.common.exception.BusinessException;

public class OrderNotFoundException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 7917668456724654772L;

    public OrderNotFoundException(final String orderId) {
        super("Order " + orderId + " not found", ORDER_NOT_FOUND, 404);
    }
}
