package com.github.npawlenko.evotingapp.security.auth;

import com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint;
import com.github.npawlenko.evotingapp.security.auth.dto.LoginRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PublicEndpoint
    @MutationMapping("login")
    public TokenResponse login(@Valid @Argument("data") LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PublicEndpoint
    @MutationMapping("register")
    public TokenResponse register(@Valid @Argument("data") RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @MutationMapping("logout")
    public void logout() {
        authService.logout();
    }

    @PublicEndpoint
    @MutationMapping("refresh")
    public TokenResponse refresh() {
        return authService.refresh();
    }
}
