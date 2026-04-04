package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.OrdemServicoRequestDTO;
import com.example.AtlazDB.model.Usuario;
import com.example.AtlazDB.model.Viatura;
import com.example.AtlazDB.repository.UsuarioRepository;
import com.example.AtlazDB.repository.ViaturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.repository.OrdemServicoRepository;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ViaturaRepository viaturaRepository;

    public OrdemServicoService(OrdemServicoRepository repository, UsuarioRepository usuarioRepository, ViaturaRepository viaturaRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.viaturaRepository = viaturaRepository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<OrdemServico> buscarPorMesEAno(int mes, int ano) {
        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fim = inicio.plusMonths(1);
        return repository.buscarPorPeriodo(inicio, fim);
    }

    public OrdemServico salvar(OrdemServicoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElseThrow(()->new RuntimeException("Usuario não encontrado!"));
        Viatura viatura = viaturaRepository.findById(dto.getIdViatura()).orElseThrow(()->new RuntimeException("Viatura não encontrada!"));

        OrdemServico os = new OrdemServico();
        os.setTipoServico(dto.getTipoServico());
        os.setLocalDestino(dto.getLocalDestino());
        os.setJustificativa(dto.getJustificativa());
        os.setRequisitante(dto.getRequisitante());
        os.setKmSaida(dto.getKmSaida());
        os.setKmChegada(dto.getKmChegada());
        os.setDataSaida(dto.getDataSaida());
        os.setDataRetorno(dto.getDataRetorno());
        os.setUsuario(usuario);
        os.setViatura(viatura);
        return repository.save(os);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public OrdemServico atualizar(Long id, OrdemServicoRequestDTO dto) {
        OrdemServico ordemServico = repository.findById(id).orElseThrow(()-> new RuntimeException("Ordem de Serviço não encontrada!"));
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElseThrow(()->new RuntimeException("Usuario não encontrado!"));
        Viatura viatura = viaturaRepository.findById(dto.getIdViatura()).orElseThrow(()->new RuntimeException("Viatura não encontrada!"));

        ordemServico.setTipoServico(dto.getTipoServico());
        ordemServico.setLocalDestino(dto.getLocalDestino());
        ordemServico.setJustificativa(dto.getJustificativa());
        ordemServico.setRequisitante(dto.getRequisitante());
        ordemServico.setKmSaida(dto.getKmSaida());
        ordemServico.setKmChegada(dto.getKmChegada());
        ordemServico.setDataSaida(dto.getDataSaida());
        ordemServico.setDataRetorno(dto.getDataRetorno());
        ordemServico.setUsuario(usuario);
        ordemServico.setViatura(viatura);
        return repository.save(ordemServico);
    }
}
