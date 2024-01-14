package com.github.npawlenko.evotingapp.utils;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserUtility {

    private final UserRepository userRepository;

    public Optional<User> getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        return userRepository.findByEmail(authentication.getName());
    }
}
