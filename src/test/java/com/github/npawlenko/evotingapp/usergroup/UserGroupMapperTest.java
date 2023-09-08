package com.github.npawlenko.evotingapp.usergroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.Role;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupRequest;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {UserGroupMapperImpl.class})
@ExtendWith(SpringExtension.class)
class UserGroupMapperTest {
    @MockBean
    private RoleMapper roleMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @MockBean
    private UserRepository userRepository;

    /**
     * Method under test: {@link UserGroupMapper#mapUserIdListToUserList(UserGroupRequest, UserGroup)}
     */
    @Test
    void testMapUserIdListToUserList() {
        when(userRepository.findByUserIds(Mockito.<List<Long>>any())).thenReturn(new ArrayList<>(){{
            add(new User());
            add(new User());
        }});
        UserGroupRequest userGroupRequest = UserGroupRequest.builder()
                .userIds(new ArrayList<>(){{
                    add(1L);
                    add(2L);
                }})
                .build();
        UserGroup userGroup = new UserGroup();

        userGroupMapper.mapUserIdListToUserList(userGroupRequest, userGroup);

        verify(userRepository).findByUserIds(Mockito.<List<Long>>any());
        assertThat(userGroup.getUsers()).hasSize(2);
    }


    @Test
    void testMapRolesInUserList() {
        RoleResponse roleResponse = new RoleResponse();
        when(roleMapper.roleToRoleResponse(Mockito.<Role>any())).thenReturn(roleResponse);
        UserGroupResponse userGroupResponse = UserGroupResponse.builder()
                .users(new ArrayList<>(){{
                    add(new UserResponse());
                }})
                .build();
        UserGroup userGroup = UserGroup.builder()
                .users(new ArrayList<>(){{
                    add(new User());
                }})
                .build();

        userGroupMapper.mapRolesInUserList(userGroup, userGroupResponse);

        verify(roleMapper).roleToRoleResponse(Mockito.<Role>any());
        assertSame(roleResponse, userGroupResponse.getUsers().get(0).getRole());
    }
}

