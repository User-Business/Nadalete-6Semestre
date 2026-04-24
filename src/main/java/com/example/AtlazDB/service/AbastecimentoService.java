package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.AbastecimentoRequestDTO;
import com.example.AtlazDB.dto.AbastecimentoResponseDTO;
import com.example.AtlazDB.enums.TipoCombustivel;
import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ViaturaRepository viaturaRepository;
    private final CidadeRepository cidadeRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public AbastecimentoService(AbastecimentoRepository repository, UsuarioRepository usuarioRepository, ViaturaRepository viaturaRepository, CidadeRepository cidadeRepository, OrdemServicoRepository ordemServicoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.viaturaRepository = viaturaRepository;
        this.cidadeRepository = cidadeRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<Abastecimento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Abastecimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Abastecimento salvar(AbastecimentoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElseThrow(()-> new RuntimeException("Usuario não encontrado."));
        Viatura viatura = viaturaRepository.findById(dto.getIdViatura()).orElseThrow(()-> new RuntimeException("Viatura não encontrada."));
        Cidade cidade = cidadeRepository.findById(dto.getIdCidade()).orElseThrow(()-> new RuntimeException("Cidade não encontrada."));
        OrdemServico os = ordemServicoRepository.findById(dto.getIdOs()).orElseThrow(()-> new RuntimeException("Ordem de serviço não encontrada."));

        Abastecimento abastecimento = new Abastecimento();
        abastecimento.setValorTotal(dto.getValorTotal());
        abastecimento.setKmAtual(os.getKmChegada());
        abastecimento.setLitros(dto.getLitros());
        abastecimento.setDataHora(dto.getDataHora());
        abastecimento.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        abastecimento.setUsuario(usuario);
        abastecimento.setViatura(viatura);
        abastecimento.setCidade(cidade);
        abastecimento.setOrdemServico(os);

        return repository.save(abastecimento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public List<Abastecimento> buscarComFiltros(
            TipoCombustivel tipoCombustivel,
            Double valorTotal,
            Double kmAtual
    ) {

        if (tipoCombustivel != null) {
            return repository.findByTipoCombustivel(tipoCombustivel);
        }

        if (valorTotal != null) {
            return repository.findByValorTotalGreaterThan(valorTotal);
        }

        if (kmAtual != null) {
            return repository.findByKmAtualGreaterThan(kmAtual);
        }

        return repository.findAll();
    }

    public Abastecimento atualizar(Long id, AbastecimentoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElseThrow(()-> new RuntimeException("Usuario não encontrado."));
        Viatura viatura = viaturaRepository.findById(dto.getIdViatura()).orElseThrow(()-> new RuntimeException("Viatura não encontrada."));
        Cidade cidade = cidadeRepository.findById(dto.getIdCidade()).orElseThrow(()-> new RuntimeException("Cidade não encontrada."));
        Abastecimento abastecimento = repository.findById(id).orElseThrow(()-> new RuntimeException("Abastecimento não encontrado."));
        OrdemServico os = ordemServicoRepository.findById(dto.getIdOs()).orElseThrow(()-> new RuntimeException("Ordem de serviço não encontrada."));


        abastecimento.setValorTotal(dto.getValorTotal());
        abastecimento.setKmAtual(os.getKmChegada());
        abastecimento.setLitros(dto.getLitros());
        abastecimento.setDataHora(dto.getDataHora());
        abastecimento.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        abastecimento.setUsuario(usuario);
        abastecimento.setViatura(viatura);
        abastecimento.setCidade(cidade);
        abastecimento.setOrdemServico(os);

        return repository.save(abastecimento);

    }
    public List<Abastecimento> buscarPorMesEAno(int mes, int ano) {

        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fim = inicio.plusMonths(1); // início do mês seguinte, exclusive

        return repository.buscarPorPeriodo(inicio, fim);
    }

    public List<AbastecimentoResponseDTO> listar() {
        List<Abastecimento> lista = repository.findAll();

        return lista.stream()
                .map(AbastecimentoResponseDTO::fromEntity)
                .toList();
    }
}