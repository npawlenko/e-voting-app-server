package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

@Mapper(
        componentModel = "spring",
        uses = {
                UserRepository.class,
                RoleMapper.class
        }
)
public abstract class UserGroupMapper {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleMapper roleMapper;

    @Mapping(source = "owner.role.role", target = "owner.role.name")
    public abstract UserGroupResponse userGroupToUserGroupResponse(UserGroup userGroup);

    @Mapping(target = "users", expression = "java( userRepository.findByUserIds(userGroupRequest.userIds()) )")
    public abstract UserGroup userGroupRequestToUserGroup(UserGroupRequest userGroupRequest);

    public abstract void updateUserGroup(UserGroupRequest userGroupRequest, @MappingTarget UserGroup userGroup);

    @AfterMapping
    protected void mapRolesInUserList(UserGroup userGroup, @MappingTarget UserGroupResponse userGroupResponse) {
        Iterator<User> userIterator = userGroup.getUsers().iterator();
        userGroupResponse.users().forEach(user -> {
            User userSource = userIterator.next();
            RoleResponse roleResponse = roleMapper.roleToRoleResponse(userSource.getRole());
            user.setRole(roleResponse);
        });
    }
}
