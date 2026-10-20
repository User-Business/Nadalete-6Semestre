package com.example.atlazdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.atlazdb.model.City;

public interface CityRepository extends JpaRepository<City, Long> {
}