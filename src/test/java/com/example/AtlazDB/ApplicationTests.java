package com.example.AtlazDB;

import com.example.AtlazDB.enums.*;
import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AbastecimentoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ModeloRepository modeloRepository;
    @Autowired private ViaturaRepository viaturaRepository;
    @Autowired private CidadeRepository cidadeRepository;
    @Autowired private OrdemServicoRepository ordemServicoRepository;

    private Long idUsuario;
    private Long idViatura;
    private Long idCidade;
    private Long idOs;

    @BeforeEach
    void setup() {
        // limpa na ordem certa por causa das FK
        ordemServicoRepository.deleteAll();
        cidadeRepository.deleteAll();
        viaturaRepository.deleteAll();
        usuarioRepository.deleteAll();
        modeloRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setMatricula("000001");
        usuario.setEmail("teste@email.com");
        usuario.setSenhaHash("senha");
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setUsuarioStatus(UsuarioStatus.ATIVO);
        idUsuario = usuarioRepository.save(usuario).getId();

        Modelo modelo = new Modelo();
        modelo.setNomeModelo("Prisma");
        modelo.setNomeMarca("Chevrolet");
        Modelo modeloSalvo = modeloRepository.save(modelo);

        Viatura viatura = new Viatura();
        viatura.setPrefixo("US01");
        viatura.setTipo(TipoViatura.UTILITARIO);
        viatura.setViaturaStatus(ViaturaStatus.ATIVO);
        viatura.setModelo(modeloSalvo);
        idViatura = viaturaRepository.save(viatura).getId();

        Cidade cidade = new Cidade();
        cidade.setNome("São José dos Campos");
        cidade.setUf("SP");
        idCidade = cidadeRepository.save(cidade).getId();

        OrdemServico os = new OrdemServico();
        os.setTipoServico(TipoOcorrencia.ADMINISTRATIVO);
        os.setLocalDestino("Taubaté");
        os.setRequisitante("Teste");
        os.setKmSaida(new BigDecimal("100"));
        os.setDataSaida(LocalDateTime.now());
        os.setUsuario(usuarioRepository.findById(idUsuario).get());
        os.setViatura(viaturaRepository.findById(idViatura).get());
        idOs = ordemServicoRepository.save(os).getId();
    }

    @Test
    void deveCriarAbastecimento() throws Exception {
        mockMvc.perform(post("/abastecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "dataHora": "2026-04-02T17:36:17",
                            "litros": 1,
                            "valorTotal": 1,
                            "numeroNotaFiscal": "teste",
                            "idUsuario": %d,
                            "idViatura": %d,
                            "idCidade": %d,
                            "idOs": %d
                        }
                        """.formatted(idUsuario, idViatura, idCidade, idOs)))
                .andExpect(status().isOk());
    }
}