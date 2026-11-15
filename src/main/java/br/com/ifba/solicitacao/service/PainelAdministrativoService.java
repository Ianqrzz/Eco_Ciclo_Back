package br.com.ifba.solicitacao.service;

import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.solicitacao.dto.PainelBacklogEstatisticasDto;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaFiltroDto;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
import br.com.ifba.solicitacao.repository.SolicitacaoColetaIRepository;
import br.com.ifba.solicitacao.repository.specification.SolicitacaoColetaSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PainelAdministrativoService implements PainelAdministrativoIService {

    private static final Logger logger = LoggerFactory.getLogger(PainelAdministrativoService.class);

    private final SolicitacaoColetaIRepository solicitacaoColetaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitacaoColeta> listarBacklog(SolicitacaoColetaFiltroDto filtro, Pageable pageable) {
        logger.info("Listando solicitações no backlog administrativo da prefeitura com filtros: {}", filtro);
        return solicitacaoColetaRepository.findAll(SolicitacaoColetaSpecification.comFiltro(filtro), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitacaoColeta obterDetalhes(Long id) {
        if (id == null) {
            throw new BusinessException("O identificador da solicitação é obrigatório.");
        }
        return solicitacaoColetaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Solicitação não encontrada no painel administrativo para o ID: " + id));
    }

    @Override
    @Transactional
    public SolicitacaoColeta atualizarStatus(Long id, StatusSolicitacao novoStatus, String observacoesEquipe, String motivoCancelamento) {
        logger.info("Atualizando status da solicitação ID {} para {}", id, novoStatus);

        if (novoStatus == null) {
            throw new BusinessException("O novo status da solicitação é obrigatório.");
        }

        SolicitacaoColeta solicitacao = obterDetalhes(id);

        if (solicitacao.getStatus().isTerminal()) {
            throw new BusinessException(String.format(
                    "Não é permitido alterar o status de uma solicitação que já foi finalizada como %s.",
                    solicitacao.getStatus().getDescricao()
            ));
        }

        // Validação da transição para cancelada
        if (novoStatus == StatusSolicitacao.CANCELADA) {
            if (motivoCancelamento == null || motivoCancelamento.trim().isBlank()) {
                throw new BusinessException("Para cancelar a solicitação, é obrigatório informar o motivo do cancelamento.");
            }
            solicitacao.setMotivoCancelamento(motivoCancelamento.trim());
        }

        // Validação da transição para agendada
        if (novoStatus == StatusSolicitacao.AGENDADA && solicitacao.getDataAgendada() == null) {
            throw new BusinessException("Para definir o status como AGENDADA, utilize a funcionalidade de agendamento com data futura e equipe responsável.");
        }

        // Se concluída, registra data de conclusão
        if (novoStatus == StatusSolicitacao.CONCLUIDA) {
            solicitacao.setDataConclusao(LocalDateTime.now());
        }

        if (observacoesEquipe != null && !observacoesEquipe.trim().isBlank()) {
            solicitacao.setObservacoesEquipe(observacoesEquipe.trim());
        }

        solicitacao.setStatus(novoStatus);
        SolicitacaoColeta salva = solicitacaoColetaRepository.save(solicitacao);
        logger.info("Status da solicitação protocolo {} atualizado com sucesso para {}", salva.getProtocolo(), novoStatus);
        return salva;
    }

    @Override
    @Transactional
    public SolicitacaoColeta agendarColeta(Long id, LocalDateTime dataAgendada, String responsavelEquipe, String observacoesEquipe) {
        logger.info("Agendando coleta para a solicitação ID {} para a data {}", id, dataAgendada);

        SolicitacaoColeta solicitacao = obterDetalhes(id);

        if (solicitacao.getStatus().isTerminal()) {
            throw new BusinessException(String.format(
                    "Não é possível agendar uma solicitação com status finalizado (%s).",
                    solicitacao.getStatus().getDescricao()
            ));
        }

        if (dataAgendada == null || dataAgendada.isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data agendada deve ser uma data e hora futura.");
        }

        if (responsavelEquipe == null || responsavelEquipe.trim().isBlank()) {
            throw new BusinessException("A identificação da equipe ou veículo responsável é obrigatória.");
        }

        solicitacao.setDataAgendada(dataAgendada);
        solicitacao.setResponsavelEquipe(responsavelEquipe.trim());
        solicitacao.setStatus(StatusSolicitacao.AGENDADA);

        if (observacoesEquipe != null && !observacoesEquipe.trim().isBlank()) {
            solicitacao.setObservacoesEquipe(observacoesEquipe.trim());
        }

        SolicitacaoColeta salva = solicitacaoColetaRepository.save(solicitacao);
        logger.info("Coleta protocolo {} agendada com sucesso para {}", salva.getProtocolo(), dataAgendada);
        return salva;
    }

    @Override
    @Transactional
    public List<SolicitacaoColeta> agendarEmLote(List<Long> solicitacoesIds, LocalDateTime dataAgendada, String responsavelEquipe, String observacoesEquipe) {
        if (solicitacoesIds == null || solicitacoesIds.isEmpty()) {
            throw new BusinessException("A lista de solicitações para agendamento em lote não pode estar vazia.");
        }

        logger.info("Agendando lote de {} solicitações para {}", solicitacoesIds.size(), dataAgendada);
        List<SolicitacaoColeta> atualizadas = new ArrayList<>();

        for (Long id : solicitacoesIds) {
            atualizadas.add(agendarColeta(id, dataAgendada, responsavelEquipe, observacoesEquipe));
        }

        return atualizadas;
    }

    @Override
    @Transactional(readOnly = true)
    public PainelBacklogEstatisticasDto obterEstatisticasBacklog() {
        logger.info("Calculando estatísticas centrais do backlog da coleta verde");

        long total = solicitacaoColetaRepository.count();
        long pendentes = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.PENDENTE);
        long emAnalise = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.EM_ANALISE);
        long agendadas = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.AGENDADA);
        long emAndamento = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.EM_ANDAMENTO);
        long concluidas = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.CONCLUIDA);
        long canceladas = solicitacaoColetaRepository.countByStatus(StatusSolicitacao.CANCELADA);

        // Agrupamento por tipo de resíduo
        Map<TipoResiduo, Long> porTipoResiduo = new EnumMap<>(TipoResiduo.class);
        for (TipoResiduo tipo : TipoResiduo.values()) {
            porTipoResiduo.put(tipo, 0L);
        }
        List<Object[]> resultadosResiduo = solicitacaoColetaRepository.countSolicitacoesByTipoResiduo();
        for (Object[] linha : resultadosResiduo) {
            TipoResiduo tipo = (TipoResiduo) linha[0];
            Long count = ((Number) linha[1]).longValue();
            porTipoResiduo.put(tipo, count);
        }

        // Agrupamento por bairro
        Map<String, Long> porBairro = new LinkedHashMap<>();
        List<Object[]> resultadosBairro = solicitacaoColetaRepository.countSolicitacoesByBairro();
        for (Object[] linha : resultadosBairro) {
            String bairro = (String) linha[0];
            Long count = ((Number) linha[1]).longValue();
            porBairro.put(bairro, count);
        }

        return PainelBacklogEstatisticasDto.builder()
                .totalSolicitacoes(total)
                .totalPendentes(pendentes)
                .totalEmAnalise(emAnalise)
                .totalAgendadas(agendadas)
                .totalEmAndamento(emAndamento)
                .totalConcluidas(concluidas)
                .totalCanceladas(canceladas)
                .porTipoResiduo(porTipoResiduo)
                .porBairro(porBairro)
                .build();
    }
}
