package com.github.npawlenko.evotingapp.exception;

import lombok.Getter;

@Getter
public class ApiRequestException extends RuntimeException {

    private final ApiRequestExceptionReason reason;
    private final Object[] args;

    public ApiRequestException(ApiRequestExceptionReason reason, Object... args) {
        this.reason = reason;
        this.args = args;
    }
}
