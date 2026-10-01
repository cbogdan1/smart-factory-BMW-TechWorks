package com.smartfactory.common.exception;

import lombok.Getter;

/**
 * Base exception class for all business logic exceptions in the Smart Factory system.
 *
 * This exception is thrown when business rules or domain constraints are violated.
 * Examples:
 * - Insufficient stock for reservation
 * - Production line capacity exceeded
 * - Invalid order status transition
 * - Vehicle not found in assembly pipeline
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     *  Gets the machine-readable error code (e.g., "INSUFFICIENT_STOCK", "CAPACITY_EXCEEDED").
     *  Used in error response JSON.
     */
    private final String errorCode;
    /**
     *  Gets the HTTP status code to return in the error response.
     *  Common values:
     *  - 400: Bad Request (validation/business logic error)
     *  - 409: Conflict (state machine violation, capacity exceeded)
     *  - 422: Unprocessable Entity (semantic error)
     */
    private final int statusCode;

    /**
     * Creates a new BusinessException with message, error code, and HTTP status code.
     *
     * @param message Human-readable error message (shown in response)
     * @param errorCode Machine-readable error code (e.g., "INSUFFICIENT_STOCK")
     * @param statusCode HTTP status code (e.g., 400 for Bad Request, 409 for Conflict)
     */
    public BusinessException(final String message, final String errorCode, final int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    /**
     * Creates a new BusinessException with message and error code.
     * Defaults to HTTP 400 Bad Request.
     *
     * @param message Human-readable error message
     * @param errorCode Machine-readable error code
     */
    public BusinessException(final String message, final String errorCode) {
        this(message, errorCode, 400);
    }

    /**
     * Creates a new BusinessException with just a message.
     * Defaults to HTTP 400 Bad Request and generic "BUSINESS_ERROR" code.
     *
     * @param message Human-readable error message
     */
    public BusinessException(final String message) {
        this(message, "BUSINESS_ERROR", 400);
    }

    /**
     * Creates a new BusinessException wrapping a root cause.
     * Defaults to HTTP 400 Bad Request and generic "BUSINESS_ERROR" code.
     *
     * @param message Human-readable error message
     * @param cause Root cause exception
     */
    public BusinessException(final String message, final Throwable cause) {
        this(message, "BUSINESS_ERROR", 400);
        initCause(cause);
    }

    /**
     * Creates a new BusinessException with message, error code, and root cause.
     *
     * @param message Human-readable error message
     * @param errorCode Machine-readable error code
     * @param cause Root cause exception
     */
    public BusinessException(final String message, final String errorCode, final Throwable cause) {
        this(message, errorCode, 400);
        initCause(cause);
    }

}

