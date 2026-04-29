package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ViaturaRequestDTO;
import com.example.AtlazDB.dto.ViaturaResponseDTO;
import com.example.AtlazDB.enums.ViaturaStatus;
import com.example.AtlazDB.model.Modelo;
import com.example.AtlazDB.repository.ModeloRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Viatura;
import com.example.AtlazDB.repository.ViaturaRepository;

@Service
public class ViaturaService {

    private final ViaturaRepository repository;
    private final ModeloRepository modeloRepository;

    public ViaturaService(ViaturaRepository repository, ModeloRepository modeloRepository) {
        this.repository = repository;
        this.modeloRepository = modeloRepository;
    }

    public List<ViaturaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(ViaturaResponseDTO::new)
                .toList();
    }

    public ViaturaResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(ViaturaResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada"));
    }

    public Viatura salvar(ViaturaRequestDTO dto) {
        Modelo modelo = modeloRepository.findById(dto.getIdModelo())
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado!"));

        Viatura viatura = new Viatura();
        viatura.setTipo(dto.getTipo());
        viatura.setPrefixo(dto.getPrefixo());
        viatura.setViaturaStatus(ViaturaStatus.ATIVO);
        viatura.setModelo(modelo);

        viatura.setTipoCombustivel(dto.getTipoCombustivel());

        return repository.save(viatura);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Viatura atualizar(Long id, ViaturaRequestDTO dto) {
        Viatura viatura = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada."));

        viatura.setTipo(dto.getTipo());
        viatura.setPrefixo(dto.getPrefixo());
        viatura.setViaturaStatus(ViaturaStatus.ATIVO);

        viatura.setTipoCombustivel(dto.getTipoCombustivel());

        return repository.save(viatura);
    }


}
