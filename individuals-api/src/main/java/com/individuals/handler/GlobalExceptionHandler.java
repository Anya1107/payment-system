package com.individuals.handler;

import com.individuals.dto.ErrorResponse;
import com.individuals.exception.CustomAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.individuals.util.Constants.UNEXPECTED_ERROR_MESSAGE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomAuthException.class)
    public ResponseEntity<ErrorResponse> handleCustomAuthException(CustomAuthException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ex.toErrorResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse()
                                .error(UNEXPECTED_ERROR_MESSAGE)
                                .status(500)
                );
    }
}
