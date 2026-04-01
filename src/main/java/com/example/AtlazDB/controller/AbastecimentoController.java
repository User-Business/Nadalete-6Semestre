package com.example.AtlazDB.controller;

import com.example.AtlazDB.enums.TipoCombustivel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.AtlazDB.service.GerarCsv;
import com.example.AtlazDB.model.Abastecimento;
import com.example.AtlazDB.service.AbastecimentoService;

@RestController
@RequestMapping("/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;
    private final GerarCsv gerarCsv;

    public AbastecimentoController(AbastecimentoService service, GerarCsv gerarCsv) {
        this.service = service;
        this.gerarCsv = gerarCsv;
    }

    @GetMapping
    public List<Abastecimento> buscar(
            @RequestParam(required = false) TipoCombustivel tipoCombustivel,
            @RequestParam(required = false) Double valorTotal,
            @RequestParam(required = false) Double kmAtual
    ) {
        return service.buscarComFiltros(tipoCombustivel, valorTotal, kmAtual);
    }


    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCSV() {

        byte[] csvBytes = gerarCsv.gerarCSV();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=abastecimento.csv")
                .header("Content-Type", "text/csv")
                .body(csvBytes);
    }

    @GetMapping("/{id}")
    public Abastecimento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Abastecimento criar(@RequestBody Abastecimento abastecimento) {
        return service.salvar(abastecimento);
    }

    @PutMapping("/{id}")
    public Abastecimento atualizar(@PathVariable Long id, @RequestBody Abastecimento abastecimento) {
        abastecimento.setId(id);
        return service.salvar(abastecimento);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/por-mes")
    public ResponseEntity<List<Abastecimento>> buscarPorMes(
            @RequestParam int mes,
            @RequestParam int ano) {

        List<Abastecimento> lista = service.buscarPorMesEAno(mes, ano);
        return ResponseEntity.ok(lista);
    }

}