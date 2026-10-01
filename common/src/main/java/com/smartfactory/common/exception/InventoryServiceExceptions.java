package com.smartfactory.common.exception;

public class InventoryServiceExceptions {
    // Part
    public static final String PART_WITH_ID_NOT_FOUND_ERROR_MESSAGE = "Part with id %s not found";
    public static final String PART_WITH_CODE_NOT_FOUND_ERROR_MESSAGE = "Part with code %s not found";
    public static final String PART_ALREADY_EXISTS_ERROR_MESSAGE = "Part with code %s already exists";
    public static final String PART_NOT_FOUND_ERROR_CODE = "PART_NOT_FOUND";
    public static final String PART_ALREADY_EXISTS_ERROR_CODE = "PART_ALREADY_EXISTS";
    // InventoryStock
    public static final String INVENTORY_STOCK_NOT_FOUND_ERROR_MESSAGE = "Inventory stock not found for part ID: ";
    public static final String INVENTORY_STOCK_NEGATIVE_QUANTITY_ERROR_MESSAGE = "Stock quantities cannot be negative";
    public static final String INVENTORY_STOCK_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE = "minimumQuantity cannot be greater than maximumQuantity";
    public static final String INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_MESSAGE = "availableQuantity cannot be greater than maximumQuantity";
    public static final String INSUFFICIENT_STOCK_ERROR_MESSAGE = "Insufficient stock for event: ";
    public static final String INVENTORY_STOCK_NOT_FOUND_ERROR_CODE = "INVENTORY_STOCK_NOT_FOUND";
    public static final String INVENTORY_STOCK_QUANTITY_NEGATIVE_ERROR_CODE = "INVENTORY_STOCK_QUANTITY_NEGATIVE_ERROR";
    public static final String INVENTORY_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE = "INVENTORY_STOCK_MINIMUM_GREATER_THAN_MAXIMUM_QUANTITY_ERROR";
    public static final String INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR_CODE = "INVENTORY_STOCK_AVAILABLE_GREATER_THAN_MAXIMUM_QUANTITY_ERROR";
    public static final String INSUFFICIENT_STOCK_ERROR_CODE = "INSUFFICIENT_STOCK";
    // Material Demands
    public static final String MATERIAL_DEMANDS_NOT_FOUND_ERROR_MESSAGE = "Material demands not found for event ID: ";
    public static final String MATERIAL_DEMANDS_NOT_FOUND_ERROR_CODE = "MATERIAL_DEMANDS_NOT_FOUND";
    // Reservation
    public static final String RESERVATION_NOT_FOUND_ERROR_MESSAGE = "Existing reservation not found for demand ID: ";
    public static final String RESERVATION_NOT_FOUND_ERROR_CODE = "RESERVATION_NOT_FOUND";
    // Production planned event
    public static final String PRODUCTION_PLANNED_EVENT_NULL_ERROR_MESSAGE = "Event cannot be null";
    public static final String PRODUCTION_PLANNED_EVENT_NULL_ERROR_CODE = "PRODUCITON_PLANNED_EVENT_NULL";

    public static final String MATERIAL_DELIVERED_EVENT_INVALID_ERROR_MESSAGE =
            "Material delivered event is null or has missing required fields";
    public static final String MATERIAL_DELIVERED_EVENT_INVALID_ERROR_CODE = "MATERIAL_DELIVERED_EVENT_INVALID";

    public static final String PARTS_DELIVERED_EVENT_INVALID_ERROR_MESSAGE =
            "Parts delivered event is null or has missing required fields";
    public static final String PARTS_DELIVERED_EVENT_INVALID_ERROR_CODE = "PARTS_DELIVERED_EVENT_INVALID";

    public static final String CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_CODE = "CANNOT_CONSUME_CANCELLED_RESERVATION";
    public static final String CANNOT_CONSUME_CANCELLED_RESERVATION_ERROR_MESSAGE = "Cannot consume reservation %s with status: %s";

    private InventoryServiceExceptions() {

    }
}
