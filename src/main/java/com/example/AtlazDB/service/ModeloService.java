package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ModeloRequestDTO;
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

    public Modelo salvar(ModeloRequestDTO dto) {
        Modelo modelo = new Modelo();
        modelo.setNomeModelo(dto.getNomeModelo());
        modelo.setNomeMarca(dto.getNomeMarca());
        return repository.save(modelo);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Modelo atualizar(Long id, ModeloRequestDTO dto) {
        Modelo modelo = repository.findById(id).orElseThrow(()-> new RuntimeException("Modelo não encontrado"));
        modelo.setNomeModelo(dto.getNomeModelo());
        modelo.setNomeMarca(dto.getNomeMarca());
        return repository.save(modelo);
    }
}
