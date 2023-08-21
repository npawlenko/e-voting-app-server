package com.github.npawlenko.evotingapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiRequestException extends RuntimeException {

    private final String message;
    private final HttpStatus httpStatus;

    public ApiRequestException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ApiRequestException(HttpStatus httpStatus) {
        this.message = httpStatus.getReasonPhrase();
        this.httpStatus = httpStatus;
    }
}
