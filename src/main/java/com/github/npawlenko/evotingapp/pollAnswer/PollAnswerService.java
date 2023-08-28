package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
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
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, poll.getCreator());

        PollAnswer pollAnswer = pollAnswerMapper.pollAnswerRequestToPollAnswer(data);
        pollAnswer.setPoll(poll);
        PollAnswer savedPollAnswer = pollAnswerRepository.save(pollAnswer);

        return pollAnswerMapper.pollAnswerToPollAnswerResponse(savedPollAnswer);
    }

    public PollAnswerResponse updatePollAnswer(Long pollAnswerId, PollAnswerRequest data) {
        User user = authenticatedUserUtility.getLoggedUser();
        PollAnswer pollAnswerBeforeUpdate = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollAnswerBeforeUpdate.getPoll().getCreator());

        PollAnswer pollAnswer = pollAnswerMapper.pollAnswerRequestToPollAnswer(data);
        pollAnswer.setPoll(pollAnswerBeforeUpdate.getPoll());
        PollAnswer savedPollAnswer = pollAnswerRepository.save(pollAnswer);

        return pollAnswerMapper.pollAnswerToPollAnswerResponse(savedPollAnswer);
    }

    public void deletePollAnswer(Long pollAnswerId) {
        User user = authenticatedUserUtility.getLoggedUser();
        PollAnswer pollAnswer = pollAnswerRepository.findById(pollAnswerId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollAnswer.getPoll().getCreator());

        pollAnswerRepository.deleteById(pollAnswerId);
    }

    public List<PollAnswerResponse> pollAnswers(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, poll.getCreator());

        return poll.getPollAnswers().stream()
                .map(pollAnswerMapper::pollAnswerToPollAnswerResponse)
                .toList();
    }
}
