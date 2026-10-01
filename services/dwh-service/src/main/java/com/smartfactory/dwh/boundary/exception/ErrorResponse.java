package com.smartfactory.dwh.boundary.exception;

import java.time.Instant;

public record ErrorResponse(String message, String code, Instant timestamp) {
    public ErrorResponse(final String message, final String code) {
        this(message, code, Instant.now());
    }
}
