package com.github.npawlenko.evotingapp.security.auth;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Role;
import com.github.npawlenko.evotingapp.model.RoleType;
import com.github.npawlenko.evotingapp.model.Token;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.role.RoleRepository;
import com.github.npawlenko.evotingapp.security.JwtService;
import com.github.npawlenko.evotingapp.security.auth.dto.LoginRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import com.github.npawlenko.evotingapp.token.TokenMapper;
import com.github.npawlenko.evotingapp.token.TokenRepository;
import com.github.npawlenko.evotingapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_CREDENTIALS_INVALID;
import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.USER_EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;


    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));

        Jwt accessToken = jwtService.generateJwtAccessToken(user);
        Jwt refreshToken = jwtService.generateJwtRefreshToken(user);
        Token token = Token.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresAt(
                        Objects.requireNonNull(refreshToken.getExpiresAt())
                                .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .user(user)
                .build();
        tokenRepository.save(token);

        return tokenMapper.tokenToTokenResponse(token);
    }

    public TokenResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent())
            throw new ApiRequestException(USER_EMAIL_ALREADY_EXISTS);

        Role userRole = roleRepository.findByRole(RoleType.USER)
                .orElseThrow();

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = userRepository.save(User.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(encodedPassword)
                .role(userRole)
                .build()
        );
        Jwt accessToken = jwtService.generateJwtAccessToken(user);
        Jwt refreshToken = jwtService.generateJwtRefreshToken(user);
        Token token = Token.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresAt(
                        instantToLocalDateTime(
                                Objects.requireNonNull(refreshToken.getExpiresAt())
                        )
                )
                .build();
        tokenRepository.save(token);

        return tokenMapper.tokenToTokenResponse(token);
    }

    private LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
