package br.com.ifba.solicitacao.controller;

import br.com.ifba.solicitacao.dto.request.SolicitacaoColetaPostRequestDto;
import br.com.ifba.solicitacao.dto.response.SolicitacaoColetaGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface SolicitacaoColetaIController {

    ResponseEntity<SolicitacaoColetaGetResponseDto> criarSolicitacao(SolicitacaoColetaPostRequestDto dto);

    ResponseEntity<SolicitacaoColetaGetResponseDto> buscarPorId(Long id);

    ResponseEntity<SolicitacaoColetaGetResponseDto> buscarPorProtocolo(String protocolo);

    ResponseEntity<Page<SolicitacaoColetaGetResponseDto>> buscarPorMorador(Long moradorId, Pageable pageable);

    ResponseEntity<SolicitacaoColetaGetResponseDto> cancelarPeloMorador(Long id, Long moradorId, String motivo);
}
