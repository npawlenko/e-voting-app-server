package com.github.npawlenko.evotingapp.usergroup.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserGroupRequest(
        @NotBlank String name,
        List<Long> userIds
) {
}
