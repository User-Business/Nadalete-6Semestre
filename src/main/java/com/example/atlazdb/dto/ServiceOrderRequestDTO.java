package com.example.atlazdb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.atlazdb.enums.OccurrenceType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceOrderRequestDTO {

    @NotNull
    private OccurrenceType serviceType;

    private String destinationLocation;
    private String justification;
    private String requester;

    @NotNull
    @PositiveOrZero
    private BigDecimal departureKm;

    @PositiveOrZero
    private BigDecimal arrivalKm;

    @NotNull
    private LocalDateTime departureDate;

    private LocalDateTime returnDate;

    @NotNull
    private Long userId;

    @NotNull
    private Long vehicleId;
}
