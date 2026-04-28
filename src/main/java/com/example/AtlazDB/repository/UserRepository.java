package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRegistration(String registration);
}