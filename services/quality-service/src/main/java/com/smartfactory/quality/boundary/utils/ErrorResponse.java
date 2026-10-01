package com.smartfactory.quality.boundary.utils;

import java.time.Instant;

public record ErrorResponse(String message, String code, Instant timestamp) {
}