package com.example.AtlazDB.repository;

import com.example.AtlazDB.enums.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Refueling;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RefuelingRepository extends JpaRepository<Refueling, Long> {

    List<Refueling> findByFuelType(FuelType fuelType);

    List<Refueling> findByTotalValueGreaterThan(Double totalValue);

    List<Refueling> findByCurrentKmGreaterThan(Double currentKm);

    @Query("SELECT r FROM Refueling r WHERE r.dateTime >= :start AND r.dateTime < :end")
    List<Refueling> findByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}