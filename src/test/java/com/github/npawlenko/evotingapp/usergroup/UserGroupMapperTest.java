package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserGroupMapperImpl.class})
@ExtendWith(SpringExtension.class)
class UserGroupMapperTest {
    @MockBean
    private RoleMapper roleMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testMapRolesInUserList() {
        RoleResponse roleResponse = new RoleResponse(1L, null);
        when(roleMapper.roleToRoleResponse(Mockito.any())).thenReturn(roleResponse);
        UserGroupResponse userGroupResponse = new UserGroupResponse(
                1L,
                null,
                new UserResponse(1L, null, null, null),
                new ArrayList<>(){{
                    add(new UserResponse());
                }}
        );
        UserGroup userGroup = UserGroup.builder()
                .users(new ArrayList<>(){{
                    add(new User());
                }})
                .build();

        userGroupMapper.mapRolesInUserList(userGroup, userGroupResponse);

        verify(roleMapper).roleToRoleResponse(Mockito.any());
        assertSame(roleResponse, userGroupResponse.users().get(0).getRole());
    }
}

