package com.github.npawlenko.evotingapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @SequenceGenerator(
            name = "tokens_id_generator",
            sequenceName = "tokens_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "tokens_id_generator",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @Column(name = "access_token", unique = true, length = 1000)
    public String accessToken;
    @Column(name = "refresh_token", unique = true, length = 1000)
    private String refreshToken;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
