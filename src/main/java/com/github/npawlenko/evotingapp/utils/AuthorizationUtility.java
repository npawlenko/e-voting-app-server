package com.github.npawlenko.evotingapp.utils;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.RoleType;
import com.github.npawlenko.evotingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class AuthorizationUtility {

    private final AuthenticatedUserUtility authenticatedUserUtility;

    public void requireAdminOrOwnerPermission(User loggedUser, User resourceOwner) {
        User user = authenticatedUserUtility.getLoggedUser();
        RoleType userRoleType = user.getRole().getRole();

        if (!userRoleType.equals(RoleType.ADMIN) && !user.equals(resourceOwner)) {
            throw new ApiRequestException(FORBIDDEN);
        }
    }

    public void requireAdminOrOwnerPermission(User resourceOwner) {
        requireAdminOrOwnerPermission(authenticatedUserUtility.getLoggedUser(), resourceOwner);
    }
}
