package br.com.ifba.solicitacao.dto;

import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import br.com.ifba.solicitacao.entity.TipoResiduo;
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
public class SolicitacaoColetaResumoDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("protocolo")
    private String protocolo;

    @JsonProperty("nomeMorador")
    private String nomeMorador;

    @JsonProperty("telefoneMorador")
    private String telefoneMorador;

    @JsonProperty("logradouro")
    private String logradouro;

    @JsonProperty("numero")
    private String numero;

    @JsonProperty("bairro")
    private String bairro;

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("tiposResiduos")
    private List<String> tiposResiduos;

    @JsonProperty("totalItens")
    private Integer totalItens;

    @JsonProperty("volumeEntulhoM3")
    private Double volumeEntulhoM3;

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

    @JsonProperty("responsavelEquipe")
    private String responsavelEquipe;

    public static SolicitacaoColetaResumoDto fromEntity(SolicitacaoColeta solicitacao) {
        if (solicitacao == null) {
            return null;
        }

        String nomeMorador = solicitacao.getMorador() != null ? solicitacao.getMorador().getNome() : null;
        String telefoneMorador = solicitacao.getMorador() != null ? solicitacao.getMorador().getTelefone() : null;

        String logradouro = null;
        String numero = null;
        String bairro = null;
        String cep = null;

        if (solicitacao.getEnderecoColeta() != null) {
            logradouro = solicitacao.getEnderecoColeta().getLogradouro();
            numero = solicitacao.getEnderecoColeta().getNumero();
            bairro = solicitacao.getEnderecoColeta().getBairro();
            cep = solicitacao.getEnderecoColeta().getCep();
        }

        List<String> tiposResiduos = solicitacao.getItens() != null
                ? solicitacao.getItens().stream()
                .map(item -> item.getTipoResiduo() != null ? item.getTipoResiduo().getDescricao() : "")
                .distinct()
                .collect(Collectors.toList())
                : Collections.emptyList();

        int totalItens = solicitacao.getItens() != null
                ? solicitacao.getItens().stream()
                .mapToInt(item -> item.getQuantidade() != null ? item.getQuantidade() : 1)
                .sum()
                : 0;

        Double volumeEntulho = solicitacao.getItens() != null
                ? solicitacao.getItens().stream()
                .filter(item -> item.getTipoResiduo() == TipoResiduo.ENTULHO_REFORMA && item.getVolumeEstimadoM3() != null)
                .mapToDouble(item -> item.getVolumeEstimadoM3())
                .sum()
                : 0.0;

        return SolicitacaoColetaResumoDto.builder()
                .id(solicitacao.getId())
                .protocolo(solicitacao.getProtocolo())
                .nomeMorador(nomeMorador)
                .telefoneMorador(telefoneMorador)
                .logradouro(logradouro)
                .numero(numero)
                .bairro(bairro)
                .cep(cep)
                .tiposResiduos(tiposResiduos)
                .totalItens(totalItens)
                .volumeEntulhoM3(volumeEntulho > 0 ? volumeEntulho : null)
                .status(solicitacao.getStatus())
                .statusDescricao(solicitacao.getStatus() != null ? solicitacao.getStatus().getDescricao() : null)
                .dataSolicitacao(solicitacao.getDataSolicitacao())
                .dataAgendada(solicitacao.getDataAgendada())
                .responsavelEquipe(solicitacao.getResponsavelEquipe())
                .build();
    }
}
