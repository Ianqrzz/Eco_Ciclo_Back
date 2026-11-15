package br.com.ifba.solicitacao.service;

import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.solicitacao.dto.PainelBacklogEstatisticasDto;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaFiltroDto;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.repository.SolicitacaoColetaIRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PainelAdministrativoServiceTest {

    @Mock
    private SolicitacaoColetaIRepository solicitacaoColetaRepository;

    @InjectMocks
    private PainelAdministrativoService painelAdministrativoService;

    private SolicitacaoColeta solicitacao;

    @BeforeEach
    void setUp() {
        solicitacao = new SolicitacaoColeta();
        solicitacao.setId(10L);
        solicitacao.setProtocolo("CV-20261115-001");
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDateTime.now().minusHours(2));
    }

    @Test
    @DisplayName("Deve listar solicitações do backlog aplicando filtros e paginação")
    void testListarBacklogComFiltro() {
        SolicitacaoColetaFiltroDto filtro = SolicitacaoColetaFiltroDto.builder()
                .status(StatusSolicitacao.PENDENTE)
                .bairro("Centro")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        List<SolicitacaoColeta> lista = List.of(solicitacao);
        Page<SolicitacaoColeta> pagina = new PageImpl<>(lista, pageable, 1);

        when(solicitacaoColetaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);

        Page<SolicitacaoColeta> resultado = painelAdministrativoService.listarBacklog(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(solicitacao, resultado.getContent().get(0));
        verify(solicitacaoColetaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve obter detalhes da solicitação por ID com sucesso")
    void testObterDetalhesExistente() {
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        SolicitacaoColeta detalhes = painelAdministrativoService.obterDetalhes(10L);

        assertNotNull(detalhes);
        assertEquals(10L, detalhes.getId());
        assertEquals("CV-20261115-001", detalhes.getProtocolo());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao buscar detalhes de ID inexistente")
    void testObterDetalhesInexistente() {
        when(solicitacaoColetaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                painelAdministrativoService.obterDetalhes(99L)
        );
    }

    @Test
    @DisplayName("Deve atualizar status para EM_ANDAMENTO com observações da equipe")
    void testAtualizarStatusComSucesso() {
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta atualizada = painelAdministrativoService.atualizarStatus(
                10L,
                StatusSolicitacao.EM_ANDAMENTO,
                "Equipe saiu para a coleta",
                null
        );

        assertEquals(StatusSolicitacao.EM_ANDAMENTO, atualizada.getStatus());
        assertEquals("Equipe saiu para a coleta", atualizada.getObservacoesEquipe());
    }

    @Test
    @DisplayName("Deve registrar data de conclusão ao atualizar status para CONCLUIDA")
    void testAtualizarStatusParaConcluidaRegistraData() {
        solicitacao.setStatus(StatusSolicitacao.EM_ANDAMENTO);
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta concluida = painelAdministrativoService.atualizarStatus(
                10L,
                StatusSolicitacao.CONCLUIDA,
                "Coleta realizada com sucesso",
                null
        );

        assertEquals(StatusSolicitacao.CONCLUIDA, concluida.getStatus());
        assertNotNull(concluida.getDataConclusao());
    }

    @Test
    @DisplayName("Deve atualizar status para CANCELADA quando motivo for informado")
    void testAtualizarStatusParaCanceladaComMotivo() {
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta cancelada = painelAdministrativoService.atualizarStatus(
                10L,
                StatusSolicitacao.CANCELADA,
                "Não foi possível acessar a residência",
                "Morador ausente após 3 tentativas"
        );

        assertEquals(StatusSolicitacao.CANCELADA, cancelada.getStatus());
        assertEquals("Morador ausente após 3 tentativas", cancelada.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao cancelar sem informar motivo")
    void testAtualizarStatusParaCanceladaSemMotivoLancaExcecao() {
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                painelAdministrativoService.atualizarStatus(10L, StatusSolicitacao.CANCELADA, null, "   ")
        );

        assertTrue(exception.getMessage().contains("obrigatório informar o motivo"));
    }

    @Test
    @DisplayName("Deve impedir alteração de status em solicitação já concluída ou cancelada")
    void testAtualizarStatusEmSolicitacaoTerminalLancaExcecao() {
        solicitacao.setStatus(StatusSolicitacao.CONCLUIDA);
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                painelAdministrativoService.atualizarStatus(10L, StatusSolicitacao.EM_ANDAMENTO, null, null)
        );

        assertTrue(exception.getMessage().contains("já foi finalizada"));
    }

    @Test
    @DisplayName("Deve agendar coleta com sucesso atribuindo data futura e responsável")
    void testAgendarColetaComSucesso() {
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(2);
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoColeta agendada = painelAdministrativoService.agendarColeta(
                10L,
                dataFutura,
                "Equipe Alpha - Caminhão 04",
                "Rota matutina"
        );

        assertEquals(StatusSolicitacao.AGENDADA, agendada.getStatus());
        assertEquals(dataFutura, agendada.getDataAgendada());
        assertEquals("Equipe Alpha - Caminhão 04", agendada.getResponsavelEquipe());
        assertEquals("Rota matutina", agendada.getObservacoesEquipe());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao agendar coleta com data no passado")
    void testAgendarColetaComDataPassadaLancaExcecao() {
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);
        when(solicitacaoColetaRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        assertThrows(BusinessException.class, () ->
                painelAdministrativoService.agendarColeta(10L, dataPassada, "Equipe 01", null)
        );
    }

    @Test
    @DisplayName("Deve agendar solicitações em lote com sucesso")
    void testAgendarEmLoteComSucesso() {
        SolicitacaoColeta sol1 = new SolicitacaoColeta();
        sol1.setId(1L);
        sol1.setStatus(StatusSolicitacao.PENDENTE);

        SolicitacaoColeta sol2 = new SolicitacaoColeta();
        sol2.setId(2L);
        sol2.setStatus(StatusSolicitacao.PENDENTE);

        LocalDateTime dataFutura = LocalDateTime.now().plusDays(3);

        when(solicitacaoColetaRepository.findById(1L)).thenReturn(Optional.of(sol1));
        when(solicitacaoColetaRepository.findById(2L)).thenReturn(Optional.of(sol2));
        when(solicitacaoColetaRepository.save(any(SolicitacaoColeta.class))).thenAnswer(i -> i.getArgument(0));

        List<SolicitacaoColeta> resultado = painelAdministrativoService.agendarEmLote(
                List.of(1L, 2L),
                dataFutura,
                "Equipe Regional Norte",
                "Coleta coletiva"
        );

        assertEquals(2, resultado.size());
        assertEquals(StatusSolicitacao.AGENDADA, resultado.get(0).getStatus());
        assertEquals(StatusSolicitacao.AGENDADA, resultado.get(1).getStatus());
    }

    @Test
    @DisplayName("Deve calcular estatísticas do backlog centralizado com métricas completas")
    void testObterEstatisticasBacklog() {
        when(solicitacaoColetaRepository.count()).thenReturn(20L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.PENDENTE)).thenReturn(5L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.EM_ANALISE)).thenReturn(3L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.AGENDADA)).thenReturn(4L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.EM_ANDAMENTO)).thenReturn(2L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.CONCLUIDA)).thenReturn(5L);
        when(solicitacaoColetaRepository.countByStatus(StatusSolicitacao.CANCELADA)).thenReturn(1L);

        List<Object[]> porResiduo = new ArrayList<>();
        porResiduo.add(new Object[]{TipoResiduo.MOVEIS, 8L});
        porResiduo.add(new Object[]{TipoResiduo.ENTULHO_REFORMA, 4L});
        when(solicitacaoColetaRepository.countSolicitacoesByTipoResiduo()).thenReturn(porResiduo);

        List<Object[]> porBairro = new ArrayList<>();
        porBairro.add(new Object[]{"Pituba", 7L});
        porBairro.add(new Object[]{"Barra", 5L});
        when(solicitacaoColetaRepository.countSolicitacoesByBairro()).thenReturn(porBairro);

        PainelBacklogEstatisticasDto stats = painelAdministrativoService.obterEstatisticasBacklog();

        assertNotNull(stats);
        assertEquals(20L, stats.getTotalSolicitacoes());
        assertEquals(5L, stats.getTotalPendentes());
        assertEquals(4L, stats.getTotalAgendadas());
        assertEquals(5L, stats.getTotalConcluidas());
        assertEquals(8L, stats.getPorTipoResiduo().get(TipoResiduo.MOVEIS));
        assertEquals(4L, stats.getPorTipoResiduo().get(TipoResiduo.ENTULHO_REFORMA));
        assertEquals(7L, stats.getPorBairro().get("Pituba"));
        assertEquals(5L, stats.getPorBairro().get("Barra"));
    }
}
