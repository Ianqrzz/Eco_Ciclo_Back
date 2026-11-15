package br.com.ifba.solicitacao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaRejeitarDto {

    @JsonProperty("motivoRejeicao")
    @NotBlank(message = "O motivo da rejeição da coleta é obrigatório.")
    @Size(min = 5, max = 500, message = "O motivo da rejeição deve conter entre 5 e 500 caracteres.")
    private String motivoRejeicao;
}
