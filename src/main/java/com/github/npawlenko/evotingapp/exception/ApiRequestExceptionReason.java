package com.github.npawlenko.evotingapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiRequestExceptionReason implements ExceptionWithReason {

    USER_NOT_LOGGED_IN("error.user.notLoggedIn", HttpStatus.UNAUTHORIZED),
    USER_CREDENTIALS_INVALID("error.user.invalidCredentials", HttpStatus.UNAUTHORIZED),
    USER_EMAIL_ALREADY_EXISTS("error.user.conflict.email", HttpStatus.CONFLICT),

    USERGROUP_USER_ALREADY_IN_GROUP("error.usergroup.userAlreadyInGroup", HttpStatus.BAD_REQUEST),
    USERGROUP_USER_NOT_IN_GROUP("error.usergroup.userNotInGroup", HttpStatus.BAD_REQUEST),

    NOT_FOUND("error.notFound", HttpStatus.NOT_FOUND),
    FORBIDDEN("error.forbidden", HttpStatus.FORBIDDEN),
    CONFLICT("error.conflict", HttpStatus.CONFLICT),

    TOKEN_MISSING("error.token.missing", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("error.token.invalid", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_ERROR("error.unknown.authentication", HttpStatus.UNAUTHORIZED);


    private final String messageKey;
    private final HttpStatus httpStatus;
}
