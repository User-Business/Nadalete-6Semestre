package com.example.atlazdb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.atlazdb.dto.UserRequestDTO;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.exception.BusinessRuleException;
import com.example.atlazdb.exception.ResourceNotFoundException;
import com.example.atlazdb.model.User;
import com.example.atlazdb.repository.ServiceOrderRepository;
import com.example.atlazdb.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final ServiceOrderRepository serviceOrderRepository;

    public UserService(UserRepository repository, ServiceOrderRepository serviceOrderRepository) {
        this.repository = repository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public List<User> listAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User save(UserRequestDTO dto) {
        User user = new User();
        apply(user, dto);
        user.setUserStatus(dto.getUserStatus() != null ? dto.getUserStatus() : UserStatus.DISPONIVEL);
        return repository.save(user);
    }

    public User update(Long id, UserRequestDTO dto) {
        User user = findById(id);

        if (dto.getUserStatus() == UserStatus.DESLIGADO
                && serviceOrderRepository.existsByUserIdAndReturnDateIsNull(id)) {
            throw new BusinessRuleException(
                    "Não é possível inativar o técnico porque ele está vinculado a uma Ordem de Serviço em andamento.");
        }

        apply(user, dto);
        if (dto.getUserStatus() != null) {
            user.setUserStatus(dto.getUserStatus());
        }
        return repository.save(user);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void apply(User user, UserRequestDTO dto) {
        user.setName(dto.getName());
        user.setRegistration(dto.getRegistrationNumber());
        user.setPasswordHash(dto.getPasswordHash());
        user.setEmail(dto.getEmail());
        user.setProfile(dto.getProfile());
        user.setCnhTypes(dto.getCnhTypes());
    }
}
