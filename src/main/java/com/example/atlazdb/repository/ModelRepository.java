package com.example.atlazdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.atlazdb.model.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
