package com.smartfactory.planning.utility;

import java.time.Instant;

public record ErrorResponse(String message, String code, Instant timestamp) {
}