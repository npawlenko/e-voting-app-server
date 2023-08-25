package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    private LocalDateTime createdAt;
    @Column(name = "closes_at")
    @Future
    private LocalDateTime closesAt;
    @Column(name = "is_public")
    private boolean isPublic;


    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    @OneToMany(mappedBy = "poll")
    private List<PollAnswer> pollAnswers;
    @OneToMany(mappedBy = "poll")
    private List<Vote> votes;
    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    private UserGroup userGroup;
}
