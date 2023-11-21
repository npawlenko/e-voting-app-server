package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetToken {

    @Id
    @SequenceGenerator(
            name = "reset_tokens_id_generator",
            sequenceName = "reset_tokens_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "reset_tokens_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    private String token;
    @Column(name = "expires_at")
    @Future
    private LocalDateTime expiresAt;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
