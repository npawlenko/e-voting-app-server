package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.model.PollAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollAnswerRepository extends JpaRepository<PollAnswer, Long> {
}
