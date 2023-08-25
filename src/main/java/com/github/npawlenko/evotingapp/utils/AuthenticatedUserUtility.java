package com.github.npawlenko.evotingapp.utils;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.AUTHENTICATION_ERROR;
import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_NOT_LOGGED_IN;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserUtility {

    private final UserRepository userRepository;

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ApiRequestException(USER_NOT_LOGGED_IN);
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiRequestException(AUTHENTICATION_ERROR));
    }
}
