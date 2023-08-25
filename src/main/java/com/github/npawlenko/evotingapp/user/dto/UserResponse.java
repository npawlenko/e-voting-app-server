package com.github.npawlenko.evotingapp.user.dto;

import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private RoleResponse role;
}
