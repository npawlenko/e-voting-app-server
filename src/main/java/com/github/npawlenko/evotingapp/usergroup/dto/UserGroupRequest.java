package com.github.npawlenko.evotingapp.usergroup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserGroupRequest(
        @NotBlank String name,
        @JsonProperty("user_ids")
        List<Long> userIds
) {
}
