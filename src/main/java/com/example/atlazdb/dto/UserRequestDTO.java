package com.example.atlazdb.dto;

import java.util.Set;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.Profile;
import com.example.atlazdb.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String registrationNumber;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String passwordHash;

    @NotNull
    private Profile profile;

    private UserStatus userStatus;

    private Set<CnhType> cnhTypes;
}
