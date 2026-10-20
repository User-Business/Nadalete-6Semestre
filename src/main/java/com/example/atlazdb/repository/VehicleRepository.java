package com.example.atlazdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.atlazdb.enums.VehicleStatus;
import com.example.atlazdb.model.Vehicle;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPrefix(String prefix);
    long countByVehicleStatus(VehicleStatus status);

}