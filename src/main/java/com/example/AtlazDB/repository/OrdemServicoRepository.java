package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.OrdemServico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @Query("SELECT o FROM OrdemServico o WHERE o.dataSaida >= :inicio AND o.dataSaida < :fim")
    List<OrdemServico> buscarPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    OrdemServico findTopByViatura_IdAndDataRetornoIsNotNullOrderByDataRetornoDesc(Long viaturaId);
}