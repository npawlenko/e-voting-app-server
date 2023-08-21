package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiRequestException("The user with provided email does not exist", HttpStatus.NOT_FOUND));
    }
}
