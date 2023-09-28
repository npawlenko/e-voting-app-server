package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PollService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;
    private final PollMapper pollMapper;
    private final PollRepository pollRepository;

    public List<PollResponse> accessibleForUserPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findAccessibleForUserPolls(user.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .toList();
    }

    public List<PollResponse> userPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findPollByCreatorId(user.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .toList();
    }

    public PollResponse createPoll(PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setCreator(user);
        Poll savedPoll = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPoll);
    }


    public PollResponse updatePoll(Long pollId, PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll pollBeforeUpdate = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollBeforeUpdate.getCreator());

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreator(pollBeforeUpdate.getCreator());
        Poll savedPollAnswer = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPollAnswer);
    }

    public void deletePoll(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll pollBeforeUpdate = pollRepository.findById(pollId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollBeforeUpdate.getCreator());

        pollRepository.deleteById(pollId);
    }
}
