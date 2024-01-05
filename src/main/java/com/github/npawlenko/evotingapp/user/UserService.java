package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_CREDENTIALS_INVALID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponse).toList();
    }
}
