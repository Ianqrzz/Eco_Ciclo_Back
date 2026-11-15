package br.com.ifba.solicitacao.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.exception.ApiExceptionHandler;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.service.SolicitacaoColetaIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoColetaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SolicitacaoColetaIService solicitacaoColetaService;

    @InjectMocks
    private SolicitacaoColetaController solicitacaoColetaController;

    private SolicitacaoColeta solicitacao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(solicitacaoColetaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        Endereco endereco = new Endereco();
        endereco.setCep("40000000");
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("10");
        endereco.setBairro("Itaigara");

        Morador morador = new Morador();
        morador.setId(1L);
        morador.setNome("Carlos Souza");
        morador.setEmail("carlos@email.com");
        morador.setTelefone("71999991111");

        ItemColeta item = ItemColeta.builder()
                .tipoResiduo(TipoResiduo.MOVEIS)
                .descricao("Guarda-roupa 4 portas")
                .quantidade(1)
                .build();

        solicitacao = new SolicitacaoColeta();
        solicitacao.setId(1L);
        solicitacao.setProtocolo("CV-20261115-ABC999");
        solicitacao.setMorador(morador);
        solicitacao.setEnderecoColeta(endereco);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.adicionarItem(item);
    }

    @Test
    @DisplayName("POST /api/v1/solicitacoes-coleta deve criar solicitação e retornar 201 Created")
    void testCriarSolicitacaoComSucesso() throws Exception {
        String jsonPayload = """
                {
                    "moradorId": 1,
                    "observacoesMorador": "Deixar na garagem",
                    "itens": [
                        {
                            "tipoResiduo": "MOVEIS",
                            "descricao": "Guarda-roupa 4 portas",
                            "quantidade": 1
                        }
                    ]
                }
                """;

        when(solicitacaoColetaService.criarSolicitacao(any(SolicitacaoColeta.class), eq(1L)))
                .thenReturn(solicitacao);

        mockMvc.perform(post("/api/v1/solicitacoes-coleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.protocolo").value("CV-20261115-ABC999"))
                .andExpect(jsonPath("$.morador.nome").value("Carlos Souza"))
                .andExpect(jsonPath("$.itens[0].tipoResiduo").value("MOVEIS"));
    }

    @Test
    @DisplayName("POST /api/v1/solicitacoes-coleta sem moradorId deve retornar 400 Bad Request")
    void testCriarSolicitacaoSemMoradorId() throws Exception {
        String jsonPayload = """
                {
                    "moradorId": null,
                    "itens": [
                        {
                            "tipoResiduo": "PNEUS",
                            "descricao": "4 pneus usados",
                            "quantidade": 4
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/solicitacoes-coleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/solicitacoes-coleta/protocolo/{protocolo} deve retornar 200 OK")
    void testBuscarPorProtocolo() throws Exception {
        when(solicitacaoColetaService.findByProtocolo("CV-20261115-ABC999")).thenReturn(solicitacao);

        mockMvc.perform(get("/api/v1/solicitacoes-coleta/protocolo/CV-20261115-ABC999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.protocolo").value("CV-20261115-ABC999"))
                .andExpect(jsonPath("$.morador.nome").value("Carlos Souza"));
    }

    @Test
    @DisplayName("PATCH /api/v1/solicitacoes-coleta/{id}/cancelar deve cancelar solicitação com sucesso")
    void testCancelarPeloMorador() throws Exception {
        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        solicitacao.setMotivoCancelamento("Não é mais necessário");

        when(solicitacaoColetaService.cancelarPeloMorador(1L, 1L, "Não é mais necessário"))
                .thenReturn(solicitacao);

        mockMvc.perform(patch("/api/v1/solicitacoes-coleta/1/cancelar")
                        .param("moradorId", "1")
                        .param("motivo", "Não é mais necessário")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"))
                .andExpect(jsonPath("$.motivoCancelamento").value("Não é mais necessário"));
    }
}
