package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.model.PollAnswer;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerRequest;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import org.mapstruct.Mapper;

@Mapper
public abstract class PollAnswerMapper {

    public abstract PollAnswerResponse pollAnswerToPollAnswerResponse(PollAnswer pollAnswer);
    public abstract PollAnswer pollAnswerRequestToPollAnswer(PollAnswerRequest pollAnswerRequest);
}
