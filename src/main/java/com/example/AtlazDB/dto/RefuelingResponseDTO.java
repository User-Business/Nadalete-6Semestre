package com.example.AtlazDB.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.AtlazDB.model.Refueling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefuelingResponseDTO {

    private Long id;
    private LocalDateTime date;
    private BigDecimal value;
    private String receiptNumber;

    public RefuelingResponseDTO() {
    }

    public RefuelingResponseDTO(Long id, LocalDateTime date, BigDecimal value, String receiptNumber) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.receiptNumber = receiptNumber;
    }


    public Long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public static RefuelingResponseDTO fromEntity(Refueling refueling) {
        return new RefuelingResponseDTO(
                refueling.getId(),
                refueling.getDateTime(),
                refueling.getTotalValue(),
                refueling.getReceiptNumber()
        );
    }
}