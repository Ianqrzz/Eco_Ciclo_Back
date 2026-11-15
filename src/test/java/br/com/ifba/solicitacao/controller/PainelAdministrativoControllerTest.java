package br.com.ifba.solicitacao.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.exception.ApiExceptionHandler;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.solicitacao.dto.request.SolicitacaoColetaFiltroDto;
import br.com.ifba.solicitacao.dto.response.PainelBacklogEstatisticasDto;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.service.PainelAdministrativoIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PainelAdministrativoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PainelAdministrativoIService painelAdministrativoService;

    @InjectMocks
    private PainelAdministrativoController painelAdministrativoController;

    private SolicitacaoColeta solicitacao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(painelAdministrativoController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        Endereco endereco = new Endereco();
        endereco.setCep("40000000");
        endereco.setLogradouro("Avenida Principal");
        endereco.setNumero("500");
        endereco.setBairro("Brotas");

        Morador morador = new Morador();
        morador.setId(1L);
        morador.setNome("Maria Silva");
        morador.setEmail("maria@email.com");
        morador.setTelefone("71988887777");

        ItemColeta item = ItemColeta.builder()
                .tipoResiduo(TipoResiduo.ELETRODOMESTICOS)
                .descricao("Geladeira antiga")
                .quantidade(1)
                .build();

        solicitacao = new SolicitacaoColeta();
        solicitacao.setId(10L);
        solicitacao.setProtocolo("CV-20261115-XYZ123");
        solicitacao.setMorador(morador);
        solicitacao.setEnderecoColeta(endereco);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now().minusHours(1));
        solicitacao.adicionarItem(item);
    }

    @Test
    @DisplayName("GET /api/v1/painel-prefeitura/solicitacoes deve retornar 200 OK com página do backlog")
    void testListarBacklog() throws Exception {
        when(painelAdministrativoService.listarBacklog(any(SolicitacaoColetaFiltroDto.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(solicitacao), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/painel-prefeitura/solicitacoes")
                        .param("status", "PENDENTE")
                        .param("bairro", "Brotas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].protocolo").value("CV-20261115-XYZ123"))
                .andExpect(jsonPath("$.content[0].nomeMorador").value("Maria Silva"))
                .andExpect(jsonPath("$.content[0].bairro").value("Brotas"))
                .andExpect(jsonPath("$.content[0].status").value("PENDENTE"));
    }

    @Test
    @DisplayName("GET /api/v1/painel-prefeitura/solicitacoes/{id} deve retornar 200 OK com detalhes completos")
    void testObterDetalhes() throws Exception {
        when(painelAdministrativoService.obterDetalhes(10L)).thenReturn(solicitacao);

        mockMvc.perform(get("/api/v1/painel-prefeitura/solicitacoes/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.protocolo").value("CV-20261115-XYZ123"))
                .andExpect(jsonPath("$.morador.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.itens[0].tipoResiduo").value("ELETRODOMESTICOS"))
                .andExpect(jsonPath("$.itens[0].descricao").value("Geladeira antiga"));
    }

    @Test
    @DisplayName("PATCH /api/v1/painel-prefeitura/solicitacoes/{id}/aceitar deve aceitar solicitação pela equipe municipal")
    void testAceitarSolicitacao() throws Exception {
        String jsonPayload = """
                {
                    "observacoesEquipe": "Aprovado para agendamento"
                }
                """;

        solicitacao.setStatus(StatusSolicitacao.ACEITA);
        solicitacao.setObservacoesEquipe("Aprovado para agendamento");

        when(painelAdministrativoService.aceitarSolicitacao(eq(10L), eq("Aprovado para agendamento")))
                .thenReturn(solicitacao);

        mockMvc.perform(patch("/api/v1/painel-prefeitura/solicitacoes/10/aceitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("ACEITA"))
                .andExpect(jsonPath("$.observacoesEquipe").value("Aprovado para agendamento"));
    }

    @Test
    @DisplayName("PATCH /api/v1/painel-prefeitura/solicitacoes/{id}/rejeitar deve rejeitar solicitação com motivo obrigatório")
    void testRejeitarSolicitacao() throws Exception {
        String jsonPayload = """
                {
                    "motivoRejeicao": "Material tóxico não recolhido pela coleta verde"
                }
                """;

        solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        solicitacao.setMotivoRejeicao("Material tóxico não recolhido pela coleta verde");

        when(painelAdministrativoService.rejeitarSolicitacao(eq(10L), eq("Material tóxico não recolhido pela coleta verde")))
                .thenReturn(solicitacao);

        mockMvc.perform(patch("/api/v1/painel-prefeitura/solicitacoes/10/rejeitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("REJEITADA"))
                .andExpect(jsonPath("$.motivoRejeicao").value("Material tóxico não recolhido pela coleta verde"));
    }

    @Test
    @DisplayName("PATCH /api/v1/painel-prefeitura/solicitacoes/{id}/status deve atualizar status com sucesso")
    void testAtualizarStatus() throws Exception {
        String jsonPayload = """
                {
                    "novoStatus": "EM_ANDAMENTO",
                    "observacoesEquipe": "Equipe a caminho"
                }
                """;

        solicitacao.setStatus(StatusSolicitacao.EM_ANDAMENTO);
        solicitacao.setObservacoesEquipe("Equipe a caminho");

        when(painelAdministrativoService.atualizarStatus(eq(10L), eq(StatusSolicitacao.EM_ANDAMENTO), eq("Equipe a caminho"), any()))
                .thenReturn(solicitacao);

        mockMvc.perform(patch("/api/v1/painel-prefeitura/solicitacoes/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.observacoesEquipe").value("Equipe a caminho"));
    }

    @Test
    @DisplayName("PATCH /api/v1/painel-prefeitura/solicitacoes/{id}/agendar deve agendar coleta com data futura")
    void testAgendarColeta() throws Exception {
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(2).withNano(0);
        String jsonPayload = """
                {
                    "dataAgendada": "2028-12-01T09:00:00",
                    "responsavelEquipe": "Equipe Sul - Rota 01",
                    "observacoesEquipe": "Coleta programada"
                }
                """;

        solicitacao.setStatus(StatusSolicitacao.AGENDADA);
        solicitacao.setDataAgendada(dataFutura);
        solicitacao.setResponsavelEquipe("Equipe Sul - Rota 01");

        when(painelAdministrativoService.agendarColeta(eq(10L), any(), eq("Equipe Sul - Rota 01"), eq("Coleta programada")))
                .thenReturn(solicitacao);

        mockMvc.perform(patch("/api/v1/painel-prefeitura/solicitacoes/10/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGENDADA"))
                .andExpect(jsonPath("$.responsavelEquipe").value("Equipe Sul - Rota 01"));
    }

    @Test
    @DisplayName("POST /api/v1/painel-prefeitura/solicitacoes/agendar-lote deve agendar múltiplas solicitações")
    void testAgendarEmLote() throws Exception {
        String jsonPayload = """
                {
                    "solicitacoesIds": [10],
                    "dataAgendada": "2028-12-01T09:00:00",
                    "responsavelEquipe": "Equipe Leste",
                    "observacoesEquipe": "Rota coletiva"
                }
                """;

        solicitacao.setStatus(StatusSolicitacao.AGENDADA);

        when(painelAdministrativoService.agendarEmLote(any(), any(), eq("Equipe Leste"), eq("Rota coletiva")))
                .thenReturn(List.of(solicitacao));

        mockMvc.perform(post("/api/v1/painel-prefeitura/solicitacoes/agendar-lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].status").value("AGENDADA"));
    }

    @Test
    @DisplayName("GET /api/v1/painel-prefeitura/estatisticas deve retornar 200 OK com métricas do backlog")
    void testObterEstatisticas() throws Exception {
        PainelBacklogEstatisticasDto stats = PainelBacklogEstatisticasDto.builder()
                .totalSolicitacoes(15L)
                .totalPendentes(5L)
                .totalAgendadas(4L)
                .totalConcluidas(6L)
                .porTipoResiduo(Map.of(TipoResiduo.MOVEIS, 8L))
                .porBairro(Map.of("Brotas", 5L))
                .build();

        when(painelAdministrativoService.obterEstatisticasBacklog()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/painel-prefeitura/estatisticas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSolicitacoes").value(15))
                .andExpect(jsonPath("$.totalPendentes").value(5))
                .andExpect(jsonPath("$.totalAgendadas").value(4))
                .andExpect(jsonPath("$.totalConcluidas").value(6))
                .andExpect(jsonPath("$.porTipoResiduo.MOVEIS").value(8))
                .andExpect(jsonPath("$.porBairro.Brotas").value(5));
    }
}
