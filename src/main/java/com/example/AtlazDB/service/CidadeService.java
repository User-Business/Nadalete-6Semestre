package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.CidadeRequestDTO;
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

    public Cidade salvar(CidadeRequestDTO dto) {
        Cidade cidade = new Cidade();
        cidade.setNome(dto.getNome());
        cidade.setUf(dto.getUf());
        return repository.save(cidade);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Cidade atualizar(Long id, CidadeRequestDTO dto) {
        Cidade cidade = repository.findById(id).orElseThrow(()-> new RuntimeException("Cidade não encontrada"));
        cidade.setNome(dto.getNome());
        cidade.setUf(dto.getUf());
        return repository.save(cidade);
    }
}
