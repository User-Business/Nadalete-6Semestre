package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.OrdemServico;

import java.util.Optional;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroOs(String numeroOs);
}
