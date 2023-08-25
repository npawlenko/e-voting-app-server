package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PollMapper {
    @Mapping(source = "creator.role.role", target = "creator.role.name")
    PollResponse pollToPollResponse(Poll poll);
}
