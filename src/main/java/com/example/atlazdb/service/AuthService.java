package com.example.atlazdb.service;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.LoginRequestDTO;
import com.example.atlazdb.dto.LoginResponseDTO;
import com.example.atlazdb.exception.InvalidCredentialsException;
import com.example.atlazdb.model.User;
import com.example.atlazdb.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .filter(found -> dto.getPassword().equals(found.getPasswordHash()))
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        return new LoginResponseDTO(user.getId(), user.getProfile().name(), user.getName());
    }
}
