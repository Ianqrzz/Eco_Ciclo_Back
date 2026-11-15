package br.com.ifba.solicitacao.dto.request;

import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaAtualizarStatusDto {

    @JsonProperty("novoStatus")
    @NotNull(message = "O novo status da solicitação é obrigatório.")
    private StatusSolicitacao novoStatus;

    @JsonProperty("observacoesEquipe")
    @Size(max = 1000, message = "As observações da equipe devem ter no máximo 1000 caracteres.")
    private String observacoesEquipe;

    @JsonProperty("motivoCancelamento")
    @Size(max = 500, message = "O motivo do cancelamento deve ter no máximo 500 caracteres.")
    private String motivoCancelamento;
}
