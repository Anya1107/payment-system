package com.userservice.exception;

import com.individuals.dto.ErrorResponse;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final int status;
    private final String error;

    public UserAlreadyExistsException(String error, int status) {
        this.error = error;
        this.status = status;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse()
                .error(error)
                .status(status);
    }
}
