package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @Id
    @SequenceGenerator(
            name = "user_groups_id_generator",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "user_groups_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    @ManyToMany
    @JoinTable(
            name = "user_group_association",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
}
