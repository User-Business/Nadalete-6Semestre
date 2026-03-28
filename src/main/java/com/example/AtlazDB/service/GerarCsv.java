package com.example.AtlazDB.service;

import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.repository.AbastecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.sql.*;
import java.util.List;

@Service
public class GerarCsv {


    private final AbastecimentoRepository repository;

    public GerarCsv(AbastecimentoRepository repository) {
        this.repository = repository;
    }

    public byte[] gerarCSV() {
            List<Abastecimento> abastecimentos = repository.findAll();
            StringBuilder csv = new StringBuilder();

            csv.append("data_hora,km_atual,litros,valor_total,tipo_combustivel,observacao_estado\n");

            for (Abastecimento abastecimento : abastecimentos) {
                csv.append(abastecimento.getDataHora()).append(",");
                csv.append(abastecimento.getKmAtual()).append(",");
                csv.append(abastecimento.getLitros()).append(",");
                csv.append(abastecimento.getValorTotal()).append(",");
                csv.append(abastecimento.getTipoCombustivel()).append(",");
                csv.append(abastecimento.getObservacaoEstado()).append("\n");
            }

            return csv.toString().getBytes();
    }
}

