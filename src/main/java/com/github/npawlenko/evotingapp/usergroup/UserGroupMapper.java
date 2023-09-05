package com.github.npawlenko.evotingapp.usergroup;

import java.util.List;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public abstract class UserGroupMapper {

    @Mapping(source = "owner.role.role", target = "owner.role.name")
    public abstract UserGroupResponse userGroupToUserGroupResponse(UserGroup userGroup);
    public abstract UserGroup userGroupRequestToUserGroup(UserGroupRequest userGroupRequest);
    public abstract void updateUserGroup(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup);

    @AfterMapping
    protected void mapUsersToIdList(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup) {
        List<User> userList = userGroupRequest.getUserIds().stream()
                .map(userId -> User.builder()
                        .id(userId)
                        .build()
                )
                .toList();
        userGroup.setUsers(userList);
    }
}
