package com.example.atlazdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nameModel;

    @NotBlank
    @Size(max = 100)
    private String nameBrand;
}
