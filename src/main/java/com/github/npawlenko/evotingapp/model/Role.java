package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

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
}
