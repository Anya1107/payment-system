package com.userservice.util;

public class Constants {
    public static final String USER_WITH_ID_NOT_FOUND_ERROR_MESSAGE = "User not found with id: ";
    public static final String USER_WITH_EMAIL_NOT_FOUND_ERROR_MESSAGE = "User not found with email: ";
    public static final String USER_ALREADY_EXISTS_ERROR_MESSAGE = "User already exists with email: ";
    public static final String COUNTRY_NOT_FOUND_ERROR_MESSAGE = "Country not found";
    public static final int NOT_FOUND_STATUS_CODE = 404;
    public static final int CONFLICT_STATUS_CODE = 409;

    public static final String USER_REGISTRATION_LOG = "Starting registration for user with email {}";
    public static final String USER_REGISTRATION_SUCCESS_LOG = "Registration process completed successfully for user with email {}";
    public static final String USER_FETCHING_BY_ID_LOG = "Fetching user by id {}";
    public static final String USER_FETCHING__BY_ID_FAILED_LOG = "User not found by id {}";
    public static final String USER_FETCHING_BY_EMAIL_LOG = "Fetching user email id {}";
    public static final String USER_FETCHING__BY_EMAIL_FAILED_LOG = "User not found by email {}";
    public static final String USER_DELETION_LOG = "Deleting user with id {}";
    public static final String USER_DELETION_FAILED_LOG = "User with id {} deleted successfully";
    public static final String USER_UPDATE_LOG = "Updating user with id {}";
    public static final String USER_UPDATE_FAILED_LOG = "User with id {} updated successfully";
}
