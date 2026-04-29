package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.model.User;

public record UserResponseDTO(
        Long id,
        String name,
        String registrationNumber,
        String email,
        Profile profile,
        UserStatus userStatus
) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getRegistration(),
                user.getEmail(),
                user.getProfile(),
                user.getUserStatus()
        );
    }
}