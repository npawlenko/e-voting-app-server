package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    @MutationMapping("createUserGroup")
    public UserGroupResponse createUserGroup(@Valid @Argument("userGroup") UserGroupRequest userGroupRequest) {
        return userGroupService.createUserGroup(userGroupRequest);
    }

    @MutationMapping("updateUserGroup")
    public UserGroupResponse updateUserGroup(
            @Argument("userGroupId") Long userGroupId,
            @Valid @Argument("userGroup") UserGroupRequest userGroupRequest
    ) {
        return userGroupService.updateUserGroup(userGroupId, userGroupRequest);
    }

    @MutationMapping("removeUserFromUserGroup")
    public UserGroupResponse removeUserFromUserGroup(
            @Argument("userGroupId") Long userGroupId,
            @Argument("userId") Long userId
    ) {
        return userGroupService.removeUserFromUserGroup(userGroupId, userId);
    }

    @MutationMapping("addUserToUserGroup")
    public UserGroupResponse addUserToUserGroup(
            @Argument("userGroupId") Long userGroupId,
            @Argument("userId") Long userId
    ) {
        return userGroupService.addUserToUserGroup(userGroupId, userId);
    }
}
