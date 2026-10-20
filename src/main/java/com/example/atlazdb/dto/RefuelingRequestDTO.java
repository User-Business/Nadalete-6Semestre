package com.example.atlazdb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefuelingRequestDTO {

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Positive
    private BigDecimal liters;

    @NotNull
    @Positive
    private BigDecimal totalValue;

    @NotBlank
    private String receiptNumber;

    @NotNull
    private Long userId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long cityId;

    private Long serviceOrderId;
}
