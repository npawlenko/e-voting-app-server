package com.github.npawlenko.evotingapp.role;

import com.github.npawlenko.evotingapp.model.Role;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RoleMapper {
    @Mapping(source = "role", target = "name")
    RoleResponse roleToRoleResponse(Role role);
}
