package com.example.atlazdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRegistration(String registration);

    Integer countByProfile(Profile profile);

    long countByProfileAndUserStatus(Profile profile, UserStatus userStatus);
}