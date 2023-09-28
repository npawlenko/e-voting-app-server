package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerRequest;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PollAnswerController {

    private final PollAnswerService pollAnswerService;

    @MutationMapping("insert_poll_answer")
    public PollAnswerResponse createPollAnswer(
            @Argument("poll_id") Long pollId,
            @Argument("object") PollAnswerRequest pollAnswer)
    {
        return pollAnswerService.createPollAnswer(pollId, pollAnswer);
    }

    @QueryMapping("poll_answer")
    public List<PollAnswerResponse> pollAnswers(@Argument("id") Long pollId) {
        return pollAnswerService.findPollAnswerById(pollId);
    }

    @MutationMapping("update_poll_answer")
    public PollAnswerResponse updatePollAnswer(
            @Argument("id") Long pollAnswerId,
            @Valid @Argument("object") PollAnswerRequest pollAnswer
    ) {
        return pollAnswerService.updatePollAnswer(pollAnswerId, pollAnswer);
    }

    @MutationMapping("delete_poll_answer")
    public void deletePollAnswer(@Argument("id") Long pollAnswerId) {
        pollAnswerService.deletePollAnswer(pollAnswerId);
    }
}
