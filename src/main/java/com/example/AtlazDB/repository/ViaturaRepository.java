package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Viatura;

import java.util.Optional;

public interface ViaturaRepository extends JpaRepository<Viatura, Long> {

    Optional<Viatura> findByPrefixo(String prefixo);
}
