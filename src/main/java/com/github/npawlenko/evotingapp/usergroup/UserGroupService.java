package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final UserGroupMapper userGroupMapper;

    public UserGroupResponse createUserGroup(UserGroupRequest userGroupRequest) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        UserGroup userGroup = userGroupMapper.userGroupRequestToUserGroup(userGroupRequest);
        userGroup.setOwner(loggedUser);
        return userGroupMapper.userGroupToUserGroupResponse(userGroupRepository.save(userGroup));
    }

    public UserGroupResponse updateUserGroup(Long userGroupId, UserGroupRequest userGroupRequest) {
        UserGroup userGroup = findUserGroupAndAuthorizeUser(userGroupId);

        userGroupMapper.updateUserGroup(userGroupRequest, userGroup);
        userGroupRepository.save(userGroup);
        return userGroupMapper.userGroupToUserGroupResponse(userGroup);
    }

    public UserGroupResponse removeUserFromUserGroup(Long userGroupId, Long userId) {
        UserGroup userGroup = findUserGroupAndAuthorizeUser(userGroupId);
        List<User> users = userGroup.getUsers().stream()
                .filter(u -> !Objects.equals(u.getId(), userId))
                .toList();
        if (users.size() == userGroup.getUsers().size()) {
            throw new ApiRequestException(USERGROUP_USER_NOT_IN_GROUP);
        }
        userGroup.setUsers(users);
        userGroupRepository.save(userGroup);
        return userGroupMapper.userGroupToUserGroupResponse(userGroup);
    }

    public UserGroupResponse addUserToUserGroup(Long userGroupId, Long userId) {
        UserGroup userGroup = findUserGroupAndAuthorizeUser(userGroupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (userGroup.getUsers().contains(user)) {
            throw new ApiRequestException(USERGROUP_USER_ALREADY_IN_GROUP);
        }
        userGroup.getUsers().add(user);
        return userGroupMapper.userGroupToUserGroupResponse(userGroup);
    }

    private UserGroup findUserGroupAndAuthorizeUser(Long userGroupId) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(loggedUser, userGroup.getOwner());
        return userGroup;
    }
}
