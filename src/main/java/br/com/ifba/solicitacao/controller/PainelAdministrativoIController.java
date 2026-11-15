package br.com.ifba.solicitacao.controller;

import br.com.ifba.solicitacao.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PainelAdministrativoIController {

    ResponseEntity<Page<SolicitacaoColetaResumoDto>> listarBacklog(SolicitacaoColetaFiltroDto filtro, Pageable pageable);

    ResponseEntity<SolicitacaoColetaGetResponseDto> obterDetalhes(Long id);

    ResponseEntity<SolicitacaoColetaGetResponseDto> atualizarStatus(Long id, SolicitacaoColetaAtualizarStatusDto dto);

    ResponseEntity<SolicitacaoColetaGetResponseDto> agendarColeta(Long id, SolicitacaoColetaAgendamentoDto dto);

    ResponseEntity<List<SolicitacaoColetaGetResponseDto>> agendarEmLote(SolicitacaoAgendamentoEmLoteDto dto);

    ResponseEntity<PainelBacklogEstatisticasDto> obterEstatisticas();
}
