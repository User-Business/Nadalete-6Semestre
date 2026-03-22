package com.example.AtlazDB.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.repository.OrdemServicoRepository;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;

    public OrdemServicoService(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public OrdemServico salvar(OrdemServico os) {
        return repository.save(os);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
