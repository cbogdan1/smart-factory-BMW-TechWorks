package com.smartfactory.common.exception;

public class QualityServiceExceptions {

    // Defect
    public static final String DEFECT_NOT_FOUND_ERROR_MESSAGE = "Defect with code '%s' not found";
    public static final String DEFECT_ALREADY_EXISTS_ERROR_MESSAGE = "Defect with code '%s' already exists";
    public static final String DEFECT_ALREADY_LINKED_ERROR_MESSAGE = "Defect '%s' is already linked to inspection '%s'";
    public static final String INVALID_DEFECT_CODE_ERROR_MESSAGE = "Invalid defect code '%s'";

    public static final String DEFECT_NOT_FOUND_ERROR_CODE = "DEFECT_NOT_FOUND";
    public static final String DEFECT_ALREADY_EXISTS_ERROR_CODE = "DEFECT_ALREADY_EXISTS";
    public static final String DEFECT_ALREADY_LINKED_ERROR_CODE = "DEFECT_ALREADY_LINKED";
    public static final String INVALID_DEFECT_CODE_ERROR_CODE = "INVALID_DEFECT_CODE";

    // Inspection
    public static final String INSPECTION_NOT_FOUND_ERROR_MESSAGE = "Inspection '%s' not found";

    public static final String INSPECTION_NOT_FOUND_ERROR_CODE = "INSPECTION_NOT_FOUND";

    // Vehicle assembled event
    public static final String INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_MESSAGE =
            "Vehicle-assembled event must not be null";

    public static final String INVALID_VEHICLE_ASSEMBLED_EVENT_ERROR_CODE = "INVALID_VEHICLE_ASSEMBLED_EVENT";

}
