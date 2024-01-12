package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserMapper;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.stream.IntStream;

@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class
        }
)
public abstract class UserGroupMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleMapper roleMapper;


    public abstract UserGroupResponse userGroupToUserGroupResponse(UserGroup userGroup);

    @Mapping(target = "users", expression = "java( userRepository.findByUserIds(userGroupRequest.userIds()) )")
    public abstract UserGroup userGroupRequestToUserGroup(UserGroupRequest userGroupRequest);

    public abstract void updateUserGroup(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup);

    @AfterMapping
    protected void mapRolesInUserList(UserGroup userGroup, @MappingTarget UserGroupResponse userGroupResponse) {
        Iterator<User> userIterator = userGroup.getUsers().iterator();
        int usersLength = userGroup.getUsers().size();
        IntStream.range(0, usersLength).forEach(index -> {
            UserResponse user = userGroupResponse.getUsers().get(index);
            User userSource = userIterator.next();
            RoleResponse roleResponse = roleMapper.roleToRoleResponse(userSource.getRole());
            userGroupResponse.getUsers().set(index, user.withRole(roleResponse));
        });
    }
}
