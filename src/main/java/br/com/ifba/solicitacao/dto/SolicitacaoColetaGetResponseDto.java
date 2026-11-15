package br.com.ifba.solicitacao.dto;

import br.com.ifba.endereco.dto.EnderecoGetResponseDto;
import br.com.ifba.infraestructure.mapper.ObjectMapperUtil;
import br.com.ifba.morador.dto.MoradorGetResponseDto;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaGetResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("protocolo")
    private String protocolo;

    @JsonProperty("morador")
    private MoradorGetResponseDto morador;

    @JsonProperty("enderecoColeta")
    private EnderecoGetResponseDto enderecoColeta;

    @JsonProperty("itens")
    private List<ItemColetaGetResponseDto> itens;

    @JsonProperty("status")
    private StatusSolicitacao status;

    @JsonProperty("statusDescricao")
    private String statusDescricao;

    @JsonProperty("dataSolicitacao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataSolicitacao;

    @JsonProperty("dataAgendada")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAgendada;

    @JsonProperty("dataConclusao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataConclusao;

    @JsonProperty("observacoesMorador")
    private String observacoesMorador;

    @JsonProperty("observacoesEquipe")
    private String observacoesEquipe;

    @JsonProperty("motivoCancelamento")
    private String motivoCancelamento;

    @JsonProperty("responsavelEquipe")
    private String responsavelEquipe;

    public static SolicitacaoColetaGetResponseDto fromEntity(SolicitacaoColeta solicitacao) {
        if (solicitacao == null) {
            return null;
        }

        MoradorGetResponseDto moradorDto = null;
        if (solicitacao.getMorador() != null) {
            moradorDto = ObjectMapperUtil.map(solicitacao.getMorador(), MoradorGetResponseDto.class);
        }

        EnderecoGetResponseDto enderecoDto = null;
        if (solicitacao.getEnderecoColeta() != null) {
            enderecoDto = ObjectMapperUtil.map(solicitacao.getEnderecoColeta(), EnderecoGetResponseDto.class);
        }

        List<ItemColetaGetResponseDto> itensDto = solicitacao.getItens() != null
                ? solicitacao.getItens().stream()
                .map(ItemColetaGetResponseDto::fromEntity)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return SolicitacaoColetaGetResponseDto.builder()
                .id(solicitacao.getId())
                .protocolo(solicitacao.getProtocolo())
                .morador(moradorDto)
                .enderecoColeta(enderecoDto)
                .itens(itensDto)
                .status(solicitacao.getStatus())
                .statusDescricao(solicitacao.getStatus() != null ? solicitacao.getStatus().getDescricao() : null)
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataAgendada(solicitacao.getDataAgendada())
                .dataConclusao(solicitacao.getDataConclusao())
                .observacoesMorador(solicitacao.getObservacoesMorador())
                .observacoesEquipe(solicitacao.getObservacoesEquipe())
                .motivoCancelamento(solicitacao.getMotivoCancelamento())
                .responsavelEquipe(solicitacao.getResponsavelEquipe())
                .build();
    }
}
