package com.example.atlazdb.service;

import static com.example.atlazdb.support.Fixtures.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.atlazdb.dto.LoginRequestDTO;
import com.example.atlazdb.dto.LoginResponseDTO;
import com.example.atlazdb.exception.InvalidCredentialsException;
import com.example.atlazdb.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "john1@atlaz.com";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginWhenCredentialsMatch() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(1L)));

        LoginResponseDTO response = authService.login(request(EMAIL, "hash-1"));

        assertThat(response).isEqualTo(new LoginResponseDTO(1L, "TECNICO", "John Tech"));
    }

    @Test
    void shouldRejectWrongPassword() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user(1L)));
        LoginRequestDTO request = request(EMAIL, "wrong");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas");
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(userRepository.findByEmail("unknown@atlaz.com")).thenReturn(Optional.empty());
        LoginRequestDTO request = request("unknown@atlaz.com", "hash-1");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    private static LoginRequestDTO request(String email, String secret) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(email);
        dto.setPassword(secret);
        return dto;
    }
}
