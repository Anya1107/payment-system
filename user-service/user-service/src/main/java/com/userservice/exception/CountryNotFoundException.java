package com.userservice.exception;

import com.individuals.dto.ErrorResponse;

public class CountryNotFoundException extends RuntimeException {
    private final int status;
    private final String error;

    public CountryNotFoundException(String error, int status) {
        this.error = error;
        this.status = status;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse()
                .error(error)
                .status(status);
    }
}
