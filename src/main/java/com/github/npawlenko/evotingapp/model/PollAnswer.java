package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "poll_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollAnswer {

    @Id
    @SequenceGenerator(
            name = "poll_answers_id_generator",
            sequenceName = "poll_answers_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "poll_answers_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @NotBlank
    private String answer;

    @ManyToOne
    @JoinColumn(name = "poll_id", referencedColumnName = "id")
    private Poll poll;
}
