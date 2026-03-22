package com.example.AtlazDB.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Modelo;
import com.example.AtlazDB.repository.ModeloRepository;

@Service
public class ModeloService {

    private final ModeloRepository repository;

    public ModeloService(ModeloRepository repository) {
        this.repository = repository;
    }

    public List<Modelo> listarTodos() {
        return repository.findAll();
    }

    public Optional<Modelo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Modelo salvar(Modelo modelo) {
        return repository.save(modelo);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
