package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.ServiceOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    @Query("SELECT o FROM ServiceOrder o WHERE o.departureDate >= :start AND o.departureDate < :end")
    List<ServiceOrder> findByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}