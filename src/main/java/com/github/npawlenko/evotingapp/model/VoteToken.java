package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "vote_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteToken {

    @Id
    @SequenceGenerator(
            name = "vote_tokens_id_generator",
            sequenceName = "vote_tokens_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "vote_tokens_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    private String token;
    @Email
    private String email;
    @ManyToOne
    @JoinColumn(name = "poll_id", referencedColumnName = "id")
    private Poll poll;
}
