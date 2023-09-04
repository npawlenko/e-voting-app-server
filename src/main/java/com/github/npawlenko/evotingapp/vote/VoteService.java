package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.PollAnswer;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.Vote;
import com.github.npawlenko.evotingapp.poll.PollRepository;
import com.github.npawlenko.evotingapp.pollAnswer.PollAnswerRepository;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.FORBIDDEN;
import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;
    private final VoteMapper voteMapper;
    private final VoteRepository voteRepository;
    private final PollAnswerRepository pollAnswerRepository;

    public VoteResponse createVote(Long pollAnswerId) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        Poll poll = pollAnswer.getPoll();
        if (!poll.isPublic() && !poll.getUserGroup().getUsers().contains(loggedUser)) {
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

    public void deleteVote(Long voteId) {
        User loggedUser = authenticatedUserUtility.getLoggedUser();
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(loggedUser, vote.getVoter());

        voteRepository.delete(vote);
    }
}
