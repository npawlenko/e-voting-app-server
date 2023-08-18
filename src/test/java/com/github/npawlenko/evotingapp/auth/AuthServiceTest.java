package com.github.npawlenko.evotingapp.auth;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.npawlenko.evotingapp.security.auth.AuthService;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.user.UserRepository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AuthService.class})
@ExtendWith(SpringExtension.class)
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    /**
     * Method under test: {@link AuthService#register(RegisterRequest)}
     */
    @Test
    void testRegister() {
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(Optional.of(new User()));

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("jane.doe@example.org");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("iloveyou");
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).findByEmail(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AuthService#register(RegisterRequest)}
     */
    @Test
    void testRegister2() {
        when(userRepository.save(Mockito.<User>any())).thenReturn(new User());
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("jane.doe@example.org");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("iloveyou");
        assertNull(authService.register(registerRequest));
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findByEmail(Mockito.<String>any());
        verify(passwordEncoder).encode(Mockito.<CharSequence>any());
    }

    /**
     * Method under test: {@link AuthService#register(RegisterRequest)}
     */
    @Test
    void testRegister3() {
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenThrow(new RuntimeException("foo"));

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("jane.doe@example.org");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("iloveyou");
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).findByEmail(Mockito.<String>any());
        verify(passwordEncoder).encode(Mockito.<CharSequence>any());
    }
}

