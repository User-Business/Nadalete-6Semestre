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
    private final ViaturaService viaturaService;

    public OrdemServicoService(OrdemServicoRepository repository,
                               UsuarioRepository usuarioRepository,
                               ViaturaRepository viaturaRepository,
                               ViaturaService viaturaService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.viaturaRepository = viaturaRepository;
        this.viaturaService = viaturaService;
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

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado!"));

        Viatura viatura = viaturaRepository.findById(dto.getIdViatura())
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada!"));

        OrdemServico os = new OrdemServico();
        os.setTipoServico(dto.getTipoServico());
        os.setLocalDestino(dto.getLocalDestino());
        os.setJustificativa(dto.getJustificativa());
        os.setRequisitante(dto.getRequisitante());
        os.setKmSaida(dto.getKmSaida());


        // Validação contra a ultima OS
        OrdemServico ultima = repository
                .findTopByViatura_IdAndDataRetornoIsNotNullOrderByDataRetornoDesc(dto.getIdViatura());

        if (ultima != null && dto.getKmChegada() != null) {

            Double kmUltimo = ultima.getKmChegada().doubleValue();

            if (dto.getKmChegada().doubleValue() < kmUltimo) {
                throw new RuntimeException(
                        "Km não pode ser menor que o último registrado (" + kmUltimo + ")"
                );
            }
        }


        // só altera kmChegada se tiver OS for finalizada
        if (dto.getKmChegada() != null && dto.getDataRetorno() != null) {
            os.setKmChegada(dto.getKmChegada());
        }

        os.setDataSaida(dto.getDataSaida());
        os.setDataRetorno(dto.getDataRetorno());
        os.setUsuario(usuario);
        os.setViatura(viatura);



        OrdemServico salva = repository.save(os);

        // atualiza km da viatura baseado na última OS
        if (salva.getKmChegada() != null) {
            viaturaService.atualizarKmAtual(salva.getViatura().getId());
        }



        return salva;
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public OrdemServico atualizar(Long id, OrdemServicoRequestDTO dto) {

        OrdemServico ordemServico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada!"));

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado!"));

        Viatura viatura = viaturaRepository.findById(dto.getIdViatura())
                .orElseThrow(() -> new RuntimeException("Viatura não encontrada!"));

        ordemServico.setTipoServico(dto.getTipoServico());
        ordemServico.setLocalDestino(dto.getLocalDestino());
        ordemServico.setJustificativa(dto.getJustificativa());
        ordemServico.setRequisitante(dto.getRequisitante());
        ordemServico.setKmSaida(dto.getKmSaida());

        OrdemServico ultima = repository
                .findTopByViatura_IdAndDataRetornoIsNotNullOrderByDataRetornoDesc(dto.getIdViatura());

        if (ultima == null || !ordemServico.getId().equals(ultima.getId())) {
            throw new RuntimeException("Só é permitido alterar a última ordem de serviço");
        }


        // Garantia de impossibilidade de edição da OS antiga
        if (dto.getKmChegada() != null) {

            Double kmAtual = viatura.getKmAtual() != null ? viatura.getKmAtual() : 0L;

            if (dto.getKmChegada().longValue() < kmAtual) {
                throw new RuntimeException(
                        "Não é possível reduzir o km da viatura. Valor atual: " + kmAtual
                );
            }
        }


        // atualiza kmChegada se OS estiver finalizada
        if (dto.getKmChegada() != null && dto.getDataRetorno() != null) {
            ordemServico.setKmChegada(dto.getKmChegada());
        }

        ordemServico.setDataSaida(dto.getDataSaida());
        ordemServico.setDataRetorno(dto.getDataRetorno());
        ordemServico.setUsuario(usuario);
        ordemServico.setViatura(viatura);

        OrdemServico salva = repository.save(ordemServico);

        // atualiza km da viatura
        if (salva.getKmChegada() != null && salva.getDataRetorno() != null) {
        }

        return salva;
    }
    }



