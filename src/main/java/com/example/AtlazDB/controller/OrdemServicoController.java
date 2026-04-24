package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.OrdemServicoRequestDTO;
import com.example.AtlazDB.dto.OrdemServicoResponseDTO;
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
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar() {
        List<OrdemServico> lista = service.listarTodos();
        
        List<OrdemServicoResponseDTO> dtoLista = lista.stream()
                .map(OrdemServicoResponseDTO::new)
                .toList();
                
        return ResponseEntity.ok(dtoLista);
    }

    @GetMapping("/tipos-ocorrencia")
    public ResponseEntity<TipoOcorrencia[]> listarTipos() {
        return ResponseEntity.ok(TipoOcorrencia.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(OrdemServicoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(@RequestBody OrdemServicoRequestDTO dto) {
        OrdemServico salvo = service.salvar(dto);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO dto) {
        OrdemServico atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorMes(
            @RequestParam int mes,
            @RequestParam int ano) {
            
        List<OrdemServico> lista = service.buscarPorMesEAno(mes, ano);
        
        List<OrdemServicoResponseDTO> dtoLista = lista.stream()
                .map(OrdemServicoResponseDTO::new)
                .toList();
                
        return ResponseEntity.ok(dtoLista);
    }
}
