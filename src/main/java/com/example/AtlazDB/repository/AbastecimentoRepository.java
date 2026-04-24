package com.example.AtlazDB.repository;

import com.example.AtlazDB.enums.TipoCombustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Abastecimento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {

    List<Abastecimento> findByTipoCombustivel(TipoCombustivel tipoCombustivel);

    List<Abastecimento> findByValorTotalGreaterThan(Double valorTotal);

    List<Abastecimento> findByKmAtualGreaterThan(Double kmAtual);



    @Query("SELECT a FROM Abastecimento a WHERE a.dataHora >= :inicio AND a.dataHora < :fim")
    List<Abastecimento> buscarPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}