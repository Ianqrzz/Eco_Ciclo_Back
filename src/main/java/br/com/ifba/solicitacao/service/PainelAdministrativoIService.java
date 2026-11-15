package br.com.ifba.solicitacao.service;

import br.com.ifba.solicitacao.dto.PainelBacklogEstatisticasDto;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaFiltroDto;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PainelAdministrativoIService {

    /**
     * Lista as solicitações para organização centralizada no backlog da prefeitura.
     * Permite filtragem dinâmica por status, tipo de resíduo, bairro, intervalo de datas e termo geral.
     */
    Page<SolicitacaoColeta> listarBacklog(SolicitacaoColetaFiltroDto filtro, Pageable pageable);

    /**
     * Obtém os detalhes completos de uma solicitação no painel administrativo.
     */
    SolicitacaoColeta obterDetalhes(Long id);

    /**
     * Atualiza o status da solicitação acompanhando o atendimento da equipe municipal.
     */
    SolicitacaoColeta atualizarStatus(Long id, StatusSolicitacao novoStatus, String observacoesEquipe, String motivoCancelamento);

    /**
     * Agenda a coleta atribuindo data e equipe responsável.
     */
    SolicitacaoColeta agendarColeta(Long id, LocalDateTime dataAgendada, String responsavelEquipe, String observacoesEquipe);

    /**
     * Organiza e agenda múltiplas solicitações em lote para otimização de rotas da prefeitura.
     */
    List<SolicitacaoColeta> agendarEmLote(List<Long> solicitacoesIds, LocalDateTime dataAgendada, String responsavelEquipe, String observacoesEquipe);

    /**
     * Retorna indicadores e métricas consolidadas do backlog da prefeitura.
     */
    PainelBacklogEstatisticasDto obterEstatisticasBacklog();
}
