package com.example.AtlazDB.repository;

import com.example.AtlazDB.enums.TipoCombustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Abastecimento;

import java.util.List;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {

    List<Abastecimento> findByTipoCombustivel(TipoCombustivel tipoCombustivel);

    List<Abastecimento> findByValorTotalGreaterThan(Double valorTotal);

    List<Abastecimento> findByKmAtualGreaterThan(Double kmAtual);
}
