package com.github.npawlenko.evotingapp.model;

import graphql.com.google.common.collect.Lists;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
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
    private LocalDateTime closesAt;
    @Column(name = "is_public")
    private boolean isPublic;


    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollAnswer> pollAnswers = Lists.newArrayList();
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = Lists.newArrayList();
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    private UserGroup userGroup;
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteToken> voteTokens;
}
