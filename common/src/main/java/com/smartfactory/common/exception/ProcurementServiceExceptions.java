package com.smartfactory.common.exception;

public class ProcurementServiceExceptions {

    //SUPPLIER
    public static final String SUPPLIER_NOT_FOUND = "SUPPLIER_NOT_FOUND";
    public static final String NO_SUPPLIER_AVAILABLE = "NO_SUPPLIER_AVAILABLE";
    public static final String SUPPLIER_NAME_EXISTS = "SUPPLIER_NAME_EXISTS";
    public static final String SUPPLIER_EMAIL_EXISTS = "SUPPLIER_EMAIL_EXISTS";
    public static final String SUPPLIER_INVALID_LEAD_TIME = "SUPPLIER_INVALID_LEAD_TIME";
    public static final String SUPPLIER_INVALID_RATING = "SUPPLIER_INVALID_RATING";

    //SUPPLIER-PARTS
    public static final String PART_NOT_FOUND = "PART_NOT_FOUND";
    public static final String SUPPLIER_PART_NOT_FOUND = "SUPPLIER_PART_NOT_FOUND";
    public static final String SUPPLIER_PART_ALREADY_EXISTS = "SUPPLIER_PART_ALREADY_EXISTS";

    //PURCHASE-ORDER
    public static final String PURCHASE_ORDER_INVALID_STATUS_TRANSITION = "PURCHASE_ORDER_INVALID_STATUS_TRANSITION";
    public static final String PURCHASE_ORDER_NOT_FOUND = "PURCHASE_ORDER_NOT_FOUND";
}
