package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @SequenceGenerator(
            name = "roles_id_generator",
            sequenceName = "roles_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "roles_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}
