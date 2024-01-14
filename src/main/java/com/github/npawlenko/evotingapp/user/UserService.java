package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.RoleType;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.user.dto.UserRequest;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticatedUserUtility authenticatedUserUtility;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponse).toList();
    }

    public List<UserResponse> getUsersPage(int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable).stream().map(userMapper::userToUserResponse).toList();
    }

    public UserResponse editUser(Long id, UserRequest data) {
        checkIsAdmin();
        User user = userRepository.findById(id).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (RoleType.ADMIN.equals(user.getRole().getRole())) {
            throw new IllegalArgumentException("Cannot edit admin account");
        }
        userMapper.updateUser(user, data);
        userRepository.save(user);
        return userMapper.userToUserResponse(user);
    }

    public void deleteUser(Long id) {
        checkIsAdmin();
        User user = userRepository.findById(id).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (RoleType.ADMIN.equals(user.getRole().getRole())) {
            throw new IllegalArgumentException("Cannot delete admin account");
        }
        userRepository.delete(user);
    }

    private void checkIsAdmin() {
        User currentUser = authenticatedUserUtility.getLoggedUser()
                .orElseThrow(() -> new ApiRequestException(FORBIDDEN));
        if (!currentUser.getRole().getRole().equals(RoleType.ADMIN)) {
            throw new ApiRequestException(FORBIDDEN);
        }
    }
}
