package com.github.npawlenko.evotingapp.resetToken;

import com.github.npawlenko.evotingapp.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
}
