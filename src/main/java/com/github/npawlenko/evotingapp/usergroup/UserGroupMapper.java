package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                UserRepository.class,
                RoleMapper.class
        }
)
public abstract class UserGroupMapper {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleMapper roleMapper;

    @Mapping(source = "owner.role.role", target = "owner.role.name")
    public abstract UserGroupResponse userGroupToUserGroupResponse(UserGroup userGroup);

    public abstract UserGroup userGroupRequestToUserGroup(UserGroupRequest userGroupRequest);

    public abstract void updateUserGroup(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup);

    @BeforeMapping
    public void mapUserIdListToUserList(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup) {
        List<User> userList = userRepository.findByUserIds(userGroupRequest.getUserIds());
        userGroup.setUsers(userList);
    }

    @AfterMapping
    protected void mapRolesInUserList(UserGroup userGroup, @MappingTarget UserGroupResponse userGroupResponse) {
        Iterator<UserResponse> userResponseIterator = userGroupResponse.getUsers().iterator();
        userGroup.getUsers().forEach(user -> {
            RoleResponse roleResponse = roleMapper.roleToRoleResponse(user.getRole());
            userResponseIterator.next().setRole(roleResponse);
        });
    }
}
