package com.github.npawlenko.evotingapp.role;

import com.github.npawlenko.evotingapp.model.Role;
import com.github.npawlenko.evotingapp.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleType roleType);
}
