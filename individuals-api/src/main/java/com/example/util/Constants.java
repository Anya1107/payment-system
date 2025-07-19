package com.example.util;

public class Constants {
    public static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "Incorrect login or password";
    public static final String KEYCLOAK_INTERNAL_ERROR_MESSAGE = "Keycloak internal error";
    public static final String VALIDATION_ERROR_MESSAGE = "Validation error";
    public static final String USER_ALREADY_EXISTS_ERROR_MESSAGE = "User already exists";
    public static final String INVALID_REFRESH_TOKEN_ERROR_MESSAGE = "Invalid or expired refresh token";
    public static final String INVALID_ACCESS_TOKEN_ERROR_MESSAGE = "Invalid token";
    public static final String USER_NOT_FOUND_ERROR_MESSAGE = "User not found";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error";
    public static final String TOKEN_WITHOUT_SUBJECT_ERROR_MESSAGE = "Token does not contain subject (sub)";
    public static final String INVALID_CLIENT_CREDENTIALS_ERROR_MESSAGE = "Invalid client credentials";

    public static final String USERNAME_PARAM = "username";
    public static final String EMAIL_PARAM = "email";
    public static final String FIRST_NAME_PARAM = "firstName";
    public static final String LAST_NAME_PARAM = "lastName";
    public static final String PASSWORD_PARAM = "password";
    public static final String ENABLED_PARAM = "enabled";
    public static final String EMAIL_VERIFIED_PARAM = "emailVerified";
    public static final String REQUIRED_ACTIONS_PARAM = "requiredActions";
    public static final String CREDENTIALS_PARAM = "credentials";
    public static final String TYPE_PARAM = "type";
    public static final String VALUE_PARAM = "value";
    public static final String TEMPORARY_PARAM = "temporary";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String SCOPE_PARAM = "scope";
    public static final String OPEN_ID_PARAM = "openid";
    public static final String REFRESH_TOKEN_PARAM = "refresh_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String TRACE_ID_PARAM = "traceId";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final Integer TOKEN_BEGIN_INDEX = 7;
    public static final String CLIENT_CREDENTIALS_PARAM = "client_credentials";
    public static final String CLIENT_SECRET_PARAM = "client_secret";

    public static final String USER_CREATING_LOG = "Creating user in Keycloak: {}";
    public static final String USER_CREATION_SUCCESS_LOG = "User created successfully: {}";
    public static final String USER_CREATION_FAILED_LOG = "Failed to create user {} in Keycloak";
    public static final String TOKEN_REFRESHING_LOG = "Attempting to refresh token";
    public static final String TOKEN_REFRESH_SUCCESS_LOG = "Token refresh successful";
    public static final String TOKEN_REFRESH_FAILED_LOG = "Token refresh failed";
    public static final String USER_FETCHING_LOG = "Fetching current user info";
    public static final String USER_FETCHING_SUCCESS_LOG = "Fetched user info: {}";
    public static final String USER_FETCHING_FAILED_LOG = "Failed to fetch current user info";
    public static final String USER_LOGIN_LOG = "Logging in user: {}";
    public static final String USER_LOGIN_SUCCESS_LOG = "User logged in successfully: {}";
    public static final String USER_LOGIN_FAILED_LOG = "Login failed";
}
