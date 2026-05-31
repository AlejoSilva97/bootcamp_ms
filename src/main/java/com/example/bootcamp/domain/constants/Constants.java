package com.example.bootcamp.domain.constants;

public class Constants {
    public static final String BOOTCAMP_ALREADY_EXISTS_CODE = "E-001";
    public static final String CAPACITY_NOT_FOUND_CODE = "E-002";
    public static final String INVALID_FIELD_CODE = "E-003";
    public static final String INTERNAL_ERROR_CODE = "E-004";
    public static final String BOOTCAMP_NOT_FOUND_CODE = "E-005";
    public static final String BOOTCAMP_CREATED = "Bootcamp created successfully";
    public static final String BOOTCAMP_NOT_FOUND = "Bootcamp with id %s not found";
    public static final String BOOTCAMP_NAME_REQUIRED = "Bootcamp name is required";
    public static final String BOOTCAMP_DESCRIPTION_REQUIRED = "Bootcamp description is required";
    public static final String BOOTCAMP_LAUNCH_DATE_REQUIRED = "Bootcamp launch date is required";
    public static final String BOOTCAMP_DURATION_INVALID = "Bootcamp duration invalid";
    public static final String BOOTCAMP_ALREADY_EXISTS = "Bootcamp with name %s already exists";
    public static final String CAPACITY_NOT_EXISTS = "The capacities list is invalid or contains one or more non-existent capacities.";
    public static final String INVALID_CAPACITY_COUNT = "A bootcamp must have between 1 and 4 associated capacities";
    public static final String INTERNAL_ERROR = "Something went wrong, please try again";
    public static final String SUCCESS_REQUESTED_DELETION = "Successfully requested deletion of external orphan capacities: {}";
    public static final String CAPACITIES_COULD_NOT_BE_DELETED = "Capacities could not be deleted externally or were already removed: {}";
    public static final String IDS_PARAMETER_REQUIRED = "The 'ids' query parameter is required and cannot be empty.";
    public static final String IDS_PARAMETER_INVALID = "The 'ids' query parameter must contain only numbers separated by commas.";
    public static final String BOOTCAMPS_NOT_FOUND = "One or more of the requested bootcamps do not exist.";
}
