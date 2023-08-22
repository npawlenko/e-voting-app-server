package com.github.npawlenko.evotingapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiRequestExceptionReason implements ExceptionWithReason {

    USER_CREDENTIALS_INVALID("error.user.invalidCredentials", HttpStatus.UNAUTHORIZED),
    USER_EMAIL_ALREADY_EXISTS("error.user.conflict.email", HttpStatus.CONFLICT),

    TOKEN_EXPIRED("error.token.expired", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("error.token.missing", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("error.token.invalid", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_ERROR("error.unknown.authentication", HttpStatus.UNAUTHORIZED);


    private final String messageKey;
    private final HttpStatus httpStatus;
}
