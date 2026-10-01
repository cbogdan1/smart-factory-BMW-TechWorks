package com.smartfactory.common.dto.inventory;

public record ReservedPart(
        String partId,
        int quantity
) {}