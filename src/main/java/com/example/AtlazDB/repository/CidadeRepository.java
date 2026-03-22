package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
}
