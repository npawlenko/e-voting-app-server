package com.github.npawlenko.evotingapp.usergroup.dto;

import com.github.npawlenko.evotingapp.user.dto.UserResponse;

import java.util.List;


public record UserGroupResponse(
        Long id,
        String name,
        UserResponse owner,
        List<UserResponse> users
) {
}
