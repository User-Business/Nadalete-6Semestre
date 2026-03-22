package com.example.AtlazDB.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Cidade;
import com.example.AtlazDB.repository.CidadeRepository;

@Service
public class CidadeService {

    private final CidadeRepository repository;

    public CidadeService(CidadeRepository repository) {
        this.repository = repository;
    }

    public List<Cidade> listarTodos() {
        return repository.findAll();
    }

    public Optional<Cidade> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Cidade salvar(Cidade cidade) {
        return repository.save(cidade);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
