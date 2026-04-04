package com.example.AtlazDB.service;

import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.repository.AbastecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

@Service
public class GerarCsv {

    private String escapar(Object valor) {
        if (valor == null) return "";
        String str = valor.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    public byte[] gerarCSV(List<OrdemServico> ordens, List<Abastecimento> abastecimentos) {
        StringBuilder csv = new StringBuilder();
        csv.append("viatura,tipo_servico,justificativa,requisitante,destino,km_saida,km_chegada,data_saida,data_retorno,litros,valor_total,nota_fiscal\n");

        for (OrdemServico os : ordens) {
            // Busca abastecimento vinculado à OS
            Abastecimento ab = abastecimentos.stream()
                    .filter(a -> a.getOrdemServico() != null && a.getOrdemServico().getId().equals(os.getId()))
                    .findFirst()
                    .orElse(null);

            csv.append(escapar(os.getViatura().getPrefixo())).append(",");
            csv.append(escapar(os.getTipoServico())).append(",");
            csv.append(escapar(os.getJustificativa())).append(",");
            csv.append(escapar(os.getRequisitante())).append(",");
            csv.append(escapar(os.getLocalDestino())).append(",");
            csv.append(escapar(os.getKmSaida())).append(",");
            csv.append(escapar(os.getKmChegada())).append(",");
            csv.append(escapar(os.getDataSaida())).append(",");
            csv.append(escapar(os.getDataRetorno())).append(",");
            csv.append(ab != null ? escapar(ab.getLitros()) : "").append(",");
            csv.append(ab != null ? escapar(ab.getValorTotal()) : "").append(",");
            csv.append(ab != null ? escapar(ab.getNumeroNotaFiscal()) : "").append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}

