package com.github.npawlenko.evotingapp.usergroup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupRequest {
    @NotBlank
    private String name;
    private List<Long> userIds;
}
