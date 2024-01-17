package com.github.npawlenko.evotingapp.usergroup;

import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.role.RoleMapper;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserGroupMapperTest {
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private VoteTokenRepository voteTokenRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupMapper userGroupMapperAbstract;

    @InjectMocks
    private UserGroupMapperImpl userGroupMapper;

    @Before
    public void setUp() throws Exception {
        // Initialize mocks created above
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapRolesInUserList() {
        RoleResponse roleResponse = new RoleResponse(1L, null);
        when(roleMapper.roleToRoleResponse(Mockito.any())).thenReturn(roleResponse);
        UserGroupResponse userGroupResponse = new UserGroupResponse(
                1L,
                null,
                new UserResponse(1L, null, null, null, null),
                new ArrayList<>() {{
                    add(new UserResponse(1L, null, null, null, null));
                }}
        );
        UserGroup userGroup = UserGroup.builder()
                .users(new ArrayList<>(){{
                    add(new User());
                }})
                .build();

        userGroupMapper.mapRolesInUserList(userGroup, userGroupResponse);

        verify(roleMapper).roleToRoleResponse(Mockito.any());
        assertSame(roleResponse, userGroupResponse.getUsers().get(0).getRole());
    }
}

