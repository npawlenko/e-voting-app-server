package com.github.npawlenko.evotingapp.utils;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason;
import com.github.npawlenko.evotingapp.model.RoleType;
import com.github.npawlenko.evotingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class AuthorizationUtility {

    private final AuthenticatedUserUtility authenticatedUserUtility;

    public void requireAdminOrOwnerPermission(User currentUser, User resourceOwner) {
        RoleType userRoleType = currentUser.getRole().getRole();

        if (!userRoleType.equals(RoleType.ADMIN) && !currentUser.equals(resourceOwner)) {
            throw new ApiRequestException(FORBIDDEN);
        }
    }

    public void requireAdminOrOwnerPermission(User resourceOwner) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        requireAdminOrOwnerPermission(currentUser, resourceOwner);
    }
}
