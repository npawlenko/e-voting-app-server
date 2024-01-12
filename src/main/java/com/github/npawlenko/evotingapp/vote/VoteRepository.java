package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoterAndPoll(User user, Poll poll);
    Optional<Vote> findByNonSystemAccountEmailAndPoll(String nonSystemAccountEmail, Poll poll);
}
