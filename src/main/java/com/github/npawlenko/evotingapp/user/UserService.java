package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_CREDENTIALS_INVALID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));
    }
}
