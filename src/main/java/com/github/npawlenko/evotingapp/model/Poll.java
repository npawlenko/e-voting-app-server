package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "polls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poll {

    @Id
    @SequenceGenerator(
            name = "polls_id_generator",
            sequenceName = "polls_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "polls_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @NotBlank
    private String question;
    @Column(name = "created_at")
    @NotBlank
    private LocalDateTime createdAt;
    @Column(name = "closes_at")
    @NotBlank
    private LocalDateTime closesAt;


    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    @OneToMany(mappedBy = "poll")
    private List<PollAnswer> pollAnswers;
    @OneToMany(mappedBy = "poll")
    private List<Vote> votes;
}
