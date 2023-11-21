package com.github.npawlenko.evotingapp.security.auth;

import com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PublicEndpoint
    @MutationMapping("auth_login")
    public TokenResponse login(
            @Email @Argument("email") String email,
            @NotBlank @Argument("password") String password
    ) {
        return authService.login(email, password);
    }

    @PublicEndpoint
    @MutationMapping("auth_register")
    public TokenResponse register(@Valid @Argument("object") RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PublicEndpoint
    @MutationMapping("auth_reset_password")
    public void resetPassword(@Argument("new_password") String newPassword, @Argument("reset_token") String token) {
        authService.resetPassword(newPassword, token);
    }

    @PublicEndpoint
    @MutationMapping("auth_send_reset_passoword_link")
    public void resetPasswordSendLink(@Argument("email") String email) {
        authService.resetPasswordSendLink(email);
    }

    @MutationMapping("auth_logout")
    public void logout() {
        authService.logout();
    }

    @PublicEndpoint
    @MutationMapping("auth_refresh")
    public TokenResponse refresh() {
        return authService.refresh();
    }
}
