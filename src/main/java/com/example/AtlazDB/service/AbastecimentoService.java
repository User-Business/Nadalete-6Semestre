package com.example.AtlazDB.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.repository.AbastecimentoRepository;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository repository;

    public AbastecimentoService(AbastecimentoRepository repository) {
        this.repository = repository;
    }

    public List<Abastecimento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Abastecimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Abastecimento salvar(Abastecimento abastecimento) {
        return repository.save(abastecimento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
