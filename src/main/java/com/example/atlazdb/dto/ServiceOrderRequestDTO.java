package com.example.atlazdb.dto;

import com.example.atlazdb.enums.OccurrenceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ServiceOrderRequestDTO {

    private OccurrenceType serviceType;
    private String destinationLocation;
    private String justification;
    private String requester;
    private BigDecimal departureKm;
    private BigDecimal arrivalKm;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private Long userId;
    private Long vehicleId;

}