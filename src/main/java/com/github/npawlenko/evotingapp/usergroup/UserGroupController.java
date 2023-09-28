package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    @QueryMapping("user_group")
    public UserGroupResponse findUserGroupById(@Argument("id") Long userGroupId) {
        return userGroupService.findUserGroupById(userGroupId);
    }

    @MutationMapping("insert_user_group")
    public UserGroupResponse createUserGroup(@Valid @Argument("object") UserGroupRequest userGroupRequest) {
        return userGroupService.createUserGroup(userGroupRequest);
    }

    @MutationMapping("update_user_group")
    public UserGroupResponse updateUserGroup(
            @Argument("id") Long userGroupId,
            @Valid @Argument("object") UserGroupRequest userGroupRequest
    ) {
        return userGroupService.updateUserGroup(userGroupId, userGroupRequest);
    }

    @MutationMapping("remove_user_from_user_group")
    public UserGroupResponse removeUserFromUserGroup(
            @Argument("user_group_id") Long userGroupId,
            @Argument("user_id") Long userId
    ) {
        return userGroupService.removeUserFromUserGroup(userGroupId, userId);
    }

    @MutationMapping("insert_user_to_user_group")
    public UserGroupResponse addUserToUserGroup(
            @Argument("user_group_id") Long userGroupId,
            @Argument("user_id") Long userId
    ) {
        return userGroupService.addUserToUserGroup(userGroupId, userId);
    }
}
