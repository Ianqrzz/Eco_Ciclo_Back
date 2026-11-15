package br.com.ifba.solicitacao.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.mapper.ObjectMapperUtil;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaGetResponseDto;
import br.com.ifba.solicitacao.dto.SolicitacaoColetaPostRequestDto;
import br.com.ifba.solicitacao.entity.ItemColeta;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.service.SolicitacaoColetaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/solicitacoes-coleta", "/solicitacoes-coleta"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitacaoColetaController implements SolicitacaoColetaIController {

    private final SolicitacaoColetaIService solicitacaoColetaService;

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> criarSolicitacao(
            @RequestBody @Valid SolicitacaoColetaPostRequestDto dto) {

        SolicitacaoColeta solicitacao = new SolicitacaoColeta();
        solicitacao.setObservacoesMorador(dto.getObservacoesMorador());

        if (dto.getEnderecoColeta() != null) {
            Endereco endereco = ObjectMapperUtil.map(dto.getEnderecoColeta(), Endereco.class);
            solicitacao.setEnderecoColeta(endereco);
        }

        if (dto.getItens() != null) {
            List<ItemColeta> itens = new ArrayList<>();
            for (var itemDto : dto.getItens()) {
                ItemColeta item = ItemColeta.builder()
                        .tipoResiduo(itemDto.getTipoResiduo())
                        .descricao(itemDto.getDescricao())
                        .quantidade(itemDto.getQuantidade())
                        .volumeEstimadoM3(itemDto.getVolumeEstimadoM3())
                        .build();
                itens.add(item);
            }
            solicitacao.setItens(itens);
        }

        SolicitacaoColeta criada = solicitacaoColetaService.criarSolicitacao(solicitacao, dto.getMoradorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SolicitacaoColetaGetResponseDto.fromEntity(criada));
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> buscarPorId(@PathVariable Long id) {
        SolicitacaoColeta solicitacao = solicitacaoColetaService.findById(id);
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @GetMapping(path = "/protocolo/{protocolo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> buscarPorProtocolo(@PathVariable String protocolo) {
        SolicitacaoColeta solicitacao = solicitacaoColetaService.findByProtocolo(protocolo);
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(solicitacao));
    }

    @Override
    @GetMapping(path = "/morador/{moradorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SolicitacaoColetaGetResponseDto>> buscarPorMorador(
            @PathVariable Long moradorId,
            Pageable pageable) {

        Page<SolicitacaoColeta> pagina = solicitacaoColetaService.findByMorador(moradorId, pageable);
        Page<SolicitacaoColetaGetResponseDto> response = pagina.map(SolicitacaoColetaGetResponseDto::fromEntity);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping(path = "/{id}/cancelar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoColetaGetResponseDto> cancelarPeloMorador(
            @PathVariable Long id,
            @RequestParam Long moradorId,
            @RequestParam(required = false) String motivo) {

        SolicitacaoColeta cancelada = solicitacaoColetaService.cancelarPeloMorador(id, moradorId, motivo);
        return ResponseEntity.ok(SolicitacaoColetaGetResponseDto.fromEntity(cancelada));
    }
}
