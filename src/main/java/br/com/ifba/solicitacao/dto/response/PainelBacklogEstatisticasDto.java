package br.com.ifba.solicitacao.dto.response;

import br.com.ifba.solicitacao.entity.TipoResiduo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PainelBacklogEstatisticasDto {

    @JsonProperty("totalSolicitacoes")
    private long totalSolicitacoes;

    @JsonProperty("totalPendentes")
    private long totalPendentes;

    @JsonProperty("totalEmAnalise")
    private long totalEmAnalise;

    @JsonProperty("totalAgendadas")
    private long totalAgendadas;

    @JsonProperty("totalEmAndamento")
    private long totalEmAndamento;

    @JsonProperty("totalConcluidas")
    private long totalConcluidas;

    @JsonProperty("totalCanceladas")
    private long totalCanceladas;

    @JsonProperty("porTipoResiduo")
    private Map<TipoResiduo, Long> porTipoResiduo;

    @JsonProperty("porBairro")
    private Map<String, Long> porBairro;
}
