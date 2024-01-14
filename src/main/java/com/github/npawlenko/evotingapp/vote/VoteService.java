package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason;
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

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

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
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(USER_NOT_LOGGED_IN));
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        Poll poll = pollAnswer.getPoll();
        if (!isEligibleForVote(poll, currentUser) || poll.getClosesAt().isBefore(LocalDateTime.now())) {
            throw new ApiRequestException(FORBIDDEN);
        }
        if (voteRepository.findByVoterAndPoll(currentUser, poll).isPresent()) {
            throw new ApiRequestException(CONFLICT);
        }

        Vote vote = Vote.builder()
                .poll(poll)
                .answer(pollAnswer)
                .castedAt(LocalDateTime.now())
                .voter(currentUser)
                .build();
        Vote savedVote = voteRepository.save(vote);
        return voteMapper.voteToVoteResponse(savedVote);
    }

    private static boolean isEligibleForVote(Poll poll, User loggedUser) {
        return (poll.isPublic() || poll.getUserGroup().getUsers().contains(loggedUser) || poll.getCreator().equals(loggedUser));
    }

    public void deleteVote(Long voteId) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(USER_NOT_LOGGED_IN));
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, vote.getVoter());

        voteRepository.delete(vote);
    }

    public VoteResponse createVoteByToken(Long pollAnswerId, String token) {
        VoteToken voteToken = voteTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        Poll poll = pollAnswer.getPoll();
        if (poll.getClosesAt().isBefore(LocalDateTime.now()) || !voteToken.getPoll().equals(poll) || voteOnPollByTokenExists(voteToken, poll)) {
            throw new ApiRequestException(FORBIDDEN);
        }

        emailUtility.sendSimpleMessage(voteToken.getEmail(),
                "Potwierdzenie oddania głosu",
                String.format(
                        "Twój głos w ankiecie \"%s\" na odpowiedź \"%s\" został oddany. Wyniki będziesz mógł przejrzeć, po zakończeniu ankiety, pod przesłanym w poprzedniej wiadomości linku.",
                        poll.getQuestion(), pollAnswer.getAnswer()));

        Vote vote = Vote.builder()
                .poll(poll)
                .answer(pollAnswer)
                .nonSystemAccountEmail(voteToken.getEmail())
                .castedAt(LocalDateTime.now())
                .build();
        Vote savedVote = voteRepository.save(vote);
        return voteMapper.voteToVoteResponse(savedVote);
    }

    public boolean voteOnPollByTokenExists(VoteToken voteToken, Poll poll) {
        return voteRepository.findByNonSystemAccountEmailAndPoll(voteToken.getEmail(), poll).isPresent();
    }
}
