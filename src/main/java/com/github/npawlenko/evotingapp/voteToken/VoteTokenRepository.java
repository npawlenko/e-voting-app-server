package com.github.npawlenko.evotingapp.voteToken;

import com.github.npawlenko.evotingapp.model.VoteToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteTokenRepository extends JpaRepository<VoteToken, Long> {
    Optional<VoteToken> findByToken(String token);
}
