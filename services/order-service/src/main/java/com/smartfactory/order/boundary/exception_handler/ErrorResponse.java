package com.smartfactory.order.boundary.exception_handler;

import java.time.Instant;

public record ErrorResponse(String message, String code, Instant timestamp) {
}