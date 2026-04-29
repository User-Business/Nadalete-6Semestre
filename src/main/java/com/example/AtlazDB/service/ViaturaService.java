package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ViaturaRequestDTO;
import com.example.AtlazDB.enums.ViaturaStatus;
import com.example.AtlazDB.model.Modelo;
import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.repository.ModeloRepository;
import com.example.AtlazDB.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Viatura;
import com.example.AtlazDB.repository.ViaturaRepository;

@Service
public class ViaturaService {

    private final ViaturaRepository repository;
    private final ModeloRepository modeloRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public ViaturaService(ViaturaRepository repository, ModeloRepository modeloRepository, OrdemServicoRepository ordemServicoRepository) {
        this.repository = repository;
        this.modeloRepository = modeloRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<Viatura> listarTodos() {
        return repository.findAll();
    }

    public Optional<Viatura> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Viatura salvar(ViaturaRequestDTO dto) {
        Modelo modelo = modeloRepository.findById(dto.getIdModelo()).orElseThrow(()->new RuntimeException("Modelo não encontrado!"));
        Viatura viatura = new Viatura();
        viatura.setTipo(dto.getTipo());
        viatura.setPrefixo(dto.getPrefixo());
        viatura.setViaturaStatus(ViaturaStatus.ATIVO);
        viatura.setModelo(modelo);

        viatura.setKmAtual((double) 0l);
        return repository.save(viatura);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Viatura atualizar(Long id, ViaturaRequestDTO dto) {
        Viatura viatura = repository.findById(id).orElseThrow(()-> new RuntimeException("Viatura não encontrada."));
        viatura.setTipo(dto.getTipo());
        viatura.setPrefixo(dto.getPrefixo());
        viatura.setViaturaStatus(ViaturaStatus.ATIVO);
        return repository.save(viatura);
    }

    public void atualizarKmAtual(Long viaturaId) {

        OrdemServico ultima = ordemServicoRepository
                .findTopByViatura_IdAndDataRetornoIsNotNullOrderByDataRetornoDesc(viaturaId);

        if (ultima != null && ultima.getKmChegada() != null) {

            Viatura viatura = repository.findById(viaturaId)
                    .orElseThrow(() -> new RuntimeException("Viatura não encontrada"));

            viatura.setKmAtual((double) ultima.getKmChegada().longValue());

            repository.save(viatura);
        }
    }
}
