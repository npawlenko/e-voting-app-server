package com.github.npawlenko.evotingapp.usergroup.dto;

import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupResponse {
        private Long id;
        private String name;
        private UserResponse owner;
        private List<UserResponse> users;
}
