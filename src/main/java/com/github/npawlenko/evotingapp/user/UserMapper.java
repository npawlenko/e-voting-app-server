package com.github.npawlenko.evotingapp.user;

import com.github.npawlenko.evotingapp.model.RoleType;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.user.dto.UserRequest;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private AuthenticatedUserUtility authenticatedUserUtility;

    @Mapping(source = "role.role", target = "role.name")
    public abstract UserResponse userToUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    public abstract void updateUser(@MappingTarget User user, UserRequest userRequest);

    @AfterMapping
    protected void afterMapping(@MappingTarget UserResponse userResponse, User user) {
        authenticatedUserUtility.getLoggedUser().ifPresentOrElse(currentUser -> {
            if(!RoleType.ADMIN.equals(currentUser.getRole().getRole())) {
                userResponse.setEmail(anonymizeEmail(user.getEmail()));
            }
        }, () -> {
            userResponse.setEmail(anonymizeEmail(user.getEmail()));
        });
    }

    private String anonymizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domainPart = parts[1];

        String anonymizedLocalPart = anonymizeLocalPart(localPart);
        String anonymizedDomainPart = anonymizeDomainPart(domainPart);

        return anonymizedLocalPart + "@" + anonymizedDomainPart;
    }

    private String anonymizeLocalPart(String localPart) {
        if (localPart.length() <= 2) {
            return "***";
        }
        return localPart.substring(0, 2) + "***";
    }

    private String anonymizeDomainPart(String domainPart) {
        int index = domainPart.indexOf('.');
        if (index <= 1) {
            return "***";
        }
        return "***" + domainPart.substring(index);
    }
}
