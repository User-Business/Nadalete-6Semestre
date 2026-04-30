package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.OccurrenceType;
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

    public OccurrenceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(OccurrenceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public BigDecimal getDepartureKm() {
        return departureKm;
    }

    public void setDepartureKm(BigDecimal departureKm) {
        this.departureKm = departureKm;
    }

    public BigDecimal getArrivalKm() {
        return arrivalKm;
    }

    public void setArrivalKm(BigDecimal arrivalKm) {
        this.arrivalKm = arrivalKm;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

}