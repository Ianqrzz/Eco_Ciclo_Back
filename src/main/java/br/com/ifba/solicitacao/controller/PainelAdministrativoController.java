package br.com.ifba.solicitacao.controller;

import br.com.ifba.solicitacao.dto.request.*;
import br.com.ifba.solicitacao.dto.response.*;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.service.PainelAdministrativoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/painel-prefeitura", "/painel-prefeitura"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PainelAdministrativoController implements PainelAdministrativoIController {

    private final PainelAdministrativoIService painelAdministrativoService;

    @Override
    @GetMapping(path = "/solicitacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SolicitacaoColetaResumoDto>> listarBacklog(
            @ModelAttribute SolicitacaoColetaFiltroDto filtro,
            Pageable pageable) {

        Page<SolicitacaoColeta> pagina = painelAdministrativoService.listarBacklog(filtro, pageable);
        Page<SolicitacaoColetaResumoDto> response = pagina.map(SolicitacaoColetaResumoDto::fromEntity);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(path = "/solicitacoes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> obterDetalhes(@PathVariable Long id) {
        SolicitacaoColeta solicitacao = painelAdministrativoService.obterDetalhes(id);
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @PatchMapping(path = "/solicitacoes/{id}/aceitar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> aceitarSolicitacao(
            @PathVariable Long id,
            @RequestBody(required = false) SolicitacaoColetaAceitarDto dto) {

        String observacoes = dto != null ? dto.getObservacoesEquipe() : null;
        SolicitacaoColeta solicitacao = painelAdministrativoService.aceitarSolicitacao(id, observacoes);
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @PatchMapping(path = "/solicitacoes/{id}/rejeitar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> rejeitarSolicitacao(
            @PathVariable Long id,
            @RequestBody @Valid SolicitacaoColetaRejeitarDto dto) {

        SolicitacaoColeta solicitacao = painelAdministrativoService.rejeitarSolicitacao(id, dto.getMotivoRejeicao());
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @PatchMapping(path = "/solicitacoes/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid SolicitacaoColetaAtualizarStatusDto dto) {

        SolicitacaoColeta solicitacao = painelAdministrativoService.atualizarStatus(
                id,
                dto.getNovoStatus(),
                dto.getObservacoesEquipe(),
                dto.getMotivoCancelamento()
        );
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @PatchMapping(path = "/solicitacoes/{id}/agendar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> agendarColeta(
            @PathVariable Long id,
            @RequestBody @Valid SolicitacaoColetaAgendamentoDto dto) {

        SolicitacaoColeta solicitacao = painelAdministrativoService.agendarColeta(
                id,
                dto.getDataAgendada(),
                dto.getResponsavelEquipe(),
                dto.getObservacoesEquipe()
        );
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @PostMapping(path = "/solicitacoes/agendar-lote", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoColetaGetResponseDto>> agendarEmLote(
            @RequestBody @Valid SolicitacaoAgendamentoEmLoteDto dto) {

        List<SolicitacaoColeta> atualizadas = painelAdministrativoService.agendarEmLote(
                dto.getSolicitacoesIds(),
                dto.getDataAgendada(),
                dto.getResponsavelEquipe(),
                dto.getObservacoesEquipe()
        );

        List<SolicitacaoColetaGetResponseDto> response = atualizadas.stream()
                .map(SolicitacaoColetaGetResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(path = "/estatisticas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PainelBacklogEstatisticasDto> obterEstatisticas() {
        PainelBacklogEstatisticasDto estatisticas = painelAdministrativoService.obterEstatisticasBacklog();
        return ResponseEntity.ok(estatisticas);
    }
}
