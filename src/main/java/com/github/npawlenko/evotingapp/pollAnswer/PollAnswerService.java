package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.PollAnswer;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.poll.PollRepository;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerRequest;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PollAnswerService {

    private final PollAnswerMapper pollAnswerMapper;
    private final PollAnswerRepository pollAnswerRepository;
    private final PollRepository pollRepository;
    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;

    public PollAnswerResponse createPollAnswer(Long pollId, PollAnswerRequest data) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, poll.getCreator());

        PollAnswer pollAnswer = pollAnswerMapper.pollAnswerRequestToPollAnswer(data);
        pollAnswer.setPoll(poll);
        PollAnswer savedPollAnswer = pollAnswerRepository.save(pollAnswer);

        return pollAnswerMapper.pollAnswerToPollAnswerResponse(savedPollAnswer);
    }

    public PollAnswerResponse updatePollAnswer(Long pollAnswerId, PollAnswerRequest data) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        PollAnswer pollAnswerBeforeUpdate = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, pollAnswerBeforeUpdate.getPoll().getCreator());

        PollAnswer pollAnswer = pollAnswerMapper.pollAnswerRequestToPollAnswer(data);
        pollAnswer.setPoll(pollAnswerBeforeUpdate.getPoll());
        PollAnswer savedPollAnswer = pollAnswerRepository.save(pollAnswer);

        return pollAnswerMapper.pollAnswerToPollAnswerResponse(savedPollAnswer);
    }

    public void deletePollAnswer(Long pollAnswerId) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, pollAnswer.getPoll().getCreator());

        pollAnswerRepository.deleteById(pollAnswerId);
    }

    public List<PollAnswerResponse> findPollAnswerById(Long pollId) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, poll.getCreator());

        return poll.getPollAnswers().stream()
                .map(pollAnswerMapper::pollAnswerToPollAnswerResponse)
                .toList();
    }
}
