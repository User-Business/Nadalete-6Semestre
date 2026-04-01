package com.example.AtlazDB.service;

import com.example.AtlazDB.enums.TipoCombustivel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Abastecimento> buscarPorMesEAno(int mes, int ano) {

        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);

        LocalDateTime fim = inicio
                .withDayOfMonth(inicio.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        return repository.buscarPorPeriodo(inicio, fim);
    }


}
