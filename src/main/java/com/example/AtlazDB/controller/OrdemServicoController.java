package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.OrdemServicoRequestDTO;
import com.example.AtlazDB.enums.TipoOcorrencia;
import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.repository.AbastecimentoRepository;
import com.example.AtlazDB.service.AbastecimentoService;
import com.example.AtlazDB.service.GerarCsv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.OrdemServico;
import com.example.AtlazDB.service.OrdemServicoService;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService service;
    private final AbastecimentoService abastecimentoService;
    private final GerarCsv gerarCsv;

    public OrdemServicoController(OrdemServicoService service,
                                  AbastecimentoService abastecimentoService,
                                  GerarCsv gerarCsv) {
        this.service = service;
        this.abastecimentoService = abastecimentoService;
        this.gerarCsv = gerarCsv;
    }

    @GetMapping
    public List<OrdemServico> listar() {
        return service.listarTodos();
    }

    @GetMapping("/tipos-ocorrencia")
    public TipoOcorrencia[] listarTipos() {
        return TipoOcorrencia.values();
    }

    @GetMapping("/{id}")
    public OrdemServico buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public OrdemServico criar(@RequestBody OrdemServicoRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    public OrdemServico atualizar(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO dto) {
        return service.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCSV(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {

        List<OrdemServico> ordens = (mes != null && ano != null)
                ? service.buscarPorMesEAno(mes, ano)
                : service.listarTodos();

        List<Abastecimento> abastecimentos = (mes != null && ano != null)
                ? abastecimentoService.buscarPorMesEAno(mes, ano)
                : abastecimentoService.listarTodos();

        byte[] csvBytes = gerarCsv.gerarCSV(ordens, abastecimentos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=registros.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csvBytes);
    }

    @GetMapping("/por-mes")
    public ResponseEntity<List<OrdemServico>> buscarPorMes(
            @RequestParam int mes,
            @RequestParam int ano) {
        return ResponseEntity.ok(service.buscarPorMesEAno(mes, ano));
    }
}
