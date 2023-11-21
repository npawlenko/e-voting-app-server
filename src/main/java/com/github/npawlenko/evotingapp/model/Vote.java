package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @SequenceGenerator(
            name = "votes_id_generator",
            sequenceName = "votes_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "votes_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @Column(name = "casted_at")
    @PastOrPresent
    private LocalDateTime castedAt;

    @ManyToOne
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private PollAnswer answer;
    @ManyToOne
    @JoinColumn(name = "poll_id", referencedColumnName = "id")
    private Poll poll;
    @ManyToOne
    @JoinColumn(name = "voter_id", referencedColumnName = "id")
    private User voter;
}
