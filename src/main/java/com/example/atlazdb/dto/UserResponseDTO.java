package com.example.atlazdb.dto;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;
import com.example.atlazdb.model.User;

import java.util.Set;

public record UserResponseDTO(

        Long id,
        String name,
        String registrationNumber,
        String email,
        Profile profile,
        UserStatus userStatus,
        Set<CnhType> cnhTypes

) {

    public UserResponseDTO(User user) {

        this(
                user.getId(),
                user.getName(),
                user.getRegistration(),
                user.getEmail(),
                user.getProfile(),
                user.getUserStatus(),
                user.getCnhTypes()
        );
    }
}
