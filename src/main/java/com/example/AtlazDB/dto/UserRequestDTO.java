package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    private String name;
    private String registrationNumber;
    private String email;
    private String passwordHash;
    private Profile profile;
    private String userStatus;

}