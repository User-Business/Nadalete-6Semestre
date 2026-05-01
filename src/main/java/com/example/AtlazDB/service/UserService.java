package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.UserRequestDTO;
import com.example.AtlazDB.enums.UserStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> listAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User save(UserRequestDTO dto) {
        User user = new User();

        user.setName(dto.getName());
        user.setRegistration(dto.getRegistration());
        user.setPasswordHash(dto.getPasswordHash());
        user.setEmail(dto.getEmail());
        user.setProfile(dto.getProfile());
        user.setUserStatus(UserStatus.DISPONIVEL);

        return repository.save(user);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public User update(Long id, UserRequestDTO dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setRegistration(dto.getRegistration());
        user.setPasswordHash(dto.getPasswordHash());
        user.setEmail(dto.getEmail());
        user.setProfile(dto.getProfile());
        user.setUserStatus(UserStatus.DISPONIVEL);

        return repository.save(user);
    }
}