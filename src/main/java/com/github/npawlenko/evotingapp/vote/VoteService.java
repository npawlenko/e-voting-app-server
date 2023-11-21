package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.*;
import com.github.npawlenko.evotingapp.pollAnswer.PollAnswerRepository;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.FORBIDDEN;
import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;
    private final EmailUtility emailUtility;
    private final VoteMapper voteMapper;
    private final VoteRepository voteRepository;
    private final PollAnswerRepository pollAnswerRepository;
    private final VoteTokenRepository voteTokenRepository;

    public VoteResponse createVote(Long pollAnswerId) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        Poll poll = pollAnswer.getPoll();
        if (!isEligibleForVote(poll, loggedUser) || poll.getClosesAt().isBefore(LocalDateTime.now())) {
            throw new ApiRequestException(FORBIDDEN);
        }

        Vote vote = Vote.builder()
                .poll(poll)
                .answer(pollAnswer)
                .castedAt(LocalDateTime.now())
                .voter(loggedUser)
                .build();
        Vote savedVote = voteRepository.save(vote);
        return voteMapper.voteToVoteResponse(savedVote);
    }

    private static boolean isEligibleForVote(Poll poll, User loggedUser) {
        return (!poll.isPublic() && !poll.getUserGroup().getUsers().contains(loggedUser));
    }

    public void deleteVote(Long voteId) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(loggedUser, vote.getVoter());

        voteRepository.delete(vote);
    }

    public VoteResponse createVoteByToken(Long pollAnswerId, String token) {
        VoteToken voteToken = voteTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        Poll poll = pollAnswer.getPoll();
        if (poll.getClosesAt().isBefore(LocalDateTime.now()) || !voteToken.getPoll().equals(poll)) {
            throw new ApiRequestException(FORBIDDEN);
        }

        emailUtility.sendSimpleMessage(voteToken.getEmail(), "Potwierdzenie oddania głosu", "Twój głos został oddany");

        Vote vote = Vote.builder()
                .poll(poll)
                .answer(pollAnswer)
                .fromSystemAccount(false)
                .nonSystemAccountEmail(voteToken.getEmail())
                .castedAt(LocalDateTime.now())
                .build();
        Vote savedVote = voteRepository.save(vote);
        return voteMapper.voteToVoteResponse(savedVote);
    }
}
