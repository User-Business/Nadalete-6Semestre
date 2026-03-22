package com.example.AtlazDB.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Viatura;
import com.example.AtlazDB.repository.ViaturaRepository;

@Service
public class ViaturaService {

    private final ViaturaRepository repository;

    public ViaturaService(ViaturaRepository repository) {
        this.repository = repository;
    }

    public List<Viatura> listarTodos() {
        return repository.findAll();
    }

    public Optional<Viatura> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Viatura salvar(Viatura viatura) {
        return repository.save(viatura);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
