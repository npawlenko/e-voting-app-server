package com.github.npawlenko.evotingapp.security.auth;

import com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint;
import com.github.npawlenko.evotingapp.security.auth.dto.LoginRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PublicEndpoint
    @MutationMapping(value = "login")
    public TokenResponse login(@Valid @Argument("data") LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PublicEndpoint
    @MutationMapping(value = "register")
    public TokenResponse register(@Valid @Argument("data") RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
