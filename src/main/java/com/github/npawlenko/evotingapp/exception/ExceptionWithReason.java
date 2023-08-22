package com.github.npawlenko.evotingapp.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionWithReason {

    String getMessageKey();
    HttpStatus getHttpStatus();

}
