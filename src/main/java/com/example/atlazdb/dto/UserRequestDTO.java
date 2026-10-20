package com.example.atlazdb.dto;

import com.example.atlazdb.enums.CnhType;
import com.example.atlazdb.enums.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRequestDTO {
    private String name;
    private String registrationNumber;
    private String email;
    private String passwordHash;
    private Profile profile;
    private String userStatus;

    private Set<CnhType> cnhTypes;

}