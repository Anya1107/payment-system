package com.userservice.exception;

import com.individuals.dto.ErrorResponse;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final int status;
    private final String error;

    public UserNotFoundException(String error, int status) {
        this.error = error;
        this.status = status;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse()
                .error(error)
                .status(status);
    }
}
