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
import com.github.npawlenko.evotingapp.utils.HttpUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final HttpUtility httpUtility;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;


    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));
        Token token = buildToken(user);

        tokenRepository.save(token);

        return tokenMapper.tokenToTokenResponse(token);
    }

    public TokenResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent())
            throw new ApiRequestException(USER_EMAIL_ALREADY_EXISTS);

        Role userRole = roleRepository.findByRole(RoleType.USER)
                .orElseThrow();

        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = userRepository.save(User.builder()
                .email(registerRequest.email())
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .password(encodedPassword)
                .role(userRole)
                .build()
        );
        Token token = buildToken(user);
        tokenRepository.save(token);

        return tokenMapper.tokenToTokenResponse(token);
    }

    public void logout() {
        HttpServletRequest request = HttpUtility.getCurrentRequest();
        String accessToken = httpUtility.getAuthorizationToken(request);

        Token token = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow();
        tokenRepository.delete(token);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public TokenResponse refresh() {
        HttpServletRequest request = HttpUtility.getCurrentRequest();
        String refreshToken = httpUtility.getAuthorizationToken(request);
        try {
            Jwt decodedToken = jwtService.decodeJwt(refreshToken);
            Token token = tokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new ApiRequestException(AUTHENTICATION_ERROR));
            User user = token.getUser();

            if (!user.getUsername().equals(decodedToken.getSubject())) {
                log.error("Username does not match token subject. {} differs from {}", user.getUsername(), decodedToken.getSubject());
                throw new ApiRequestException(AUTHENTICATION_ERROR);
            }
            authorizeUser(request, user);

            Token newToken = buildToken(user);
            tokenRepository.delete(token);
            tokenRepository.save(newToken);

            return tokenMapper.tokenToTokenResponse(newToken);
        } catch (JwtException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ApiRequestException(TOKEN_INVALID);
        }
    }

    private void authorizeUser(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Token buildToken(User user) {
        Jwt accessToken = jwtService.generateJwtAccessToken(user);
        Jwt refreshToken = jwtService.generateJwtRefreshToken(user);
        return Token.builder()
                .user(user)
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresAt(
                        instantToLocalDateTime(
                                Objects.requireNonNull(refreshToken.getExpiresAt())
                        )
                )
                .build();
    }

    private LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
