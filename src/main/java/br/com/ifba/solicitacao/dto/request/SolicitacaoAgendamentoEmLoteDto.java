package br.com.ifba.solicitacao.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAgendamentoEmLoteDto {

    @JsonProperty("solicitacoesIds")
    @NotEmpty(message = "A lista de IDs das solicitações não pode ser vazia.")
    private List<Long> solicitacoesIds;

    @JsonProperty("dataAgendada")
    @NotNull(message = "A data de agendamento é obrigatória.")
    @Future(message = "A data de agendamento deve ser uma data futura.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAgendada;

    @JsonProperty("responsavelEquipe")
    @NotBlank(message = "A equipe ou responsável pela coleta deve ser informado.")
    @Size(max = 150, message = "O nome da equipe/responsável deve ter no máximo 150 caracteres.")
    private String responsavelEquipe;

    @JsonProperty("observacoesEquipe")
    @Size(max = 1000, message = "As observações da equipe devem ter no máximo 1000 caracteres.")
    private String observacoesEquipe;
}
