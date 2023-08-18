package com.github.npawlenko.evotingapp.token;

import com.github.npawlenko.evotingapp.model.Token;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TokenMapper {
    TokenResponse tokenToTokenResponse(Token token);
}
