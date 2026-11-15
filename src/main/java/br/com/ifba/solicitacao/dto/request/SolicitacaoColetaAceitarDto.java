package br.com.ifba.solicitacao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaAceitarDto {

    @JsonProperty("observacoesEquipe")
    @Size(max = 1000, message = "As observações da equipe devem ter no máximo 1000 caracteres.")
    private String observacoesEquipe;
}
