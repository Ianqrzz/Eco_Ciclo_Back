package br.com.ifba.solicitacao.dto.request;

import br.com.ifba.solicitacao.entity.TipoResiduo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ItemColetaPostRequestDto {

    @JsonProperty("tipoResiduo")
    @NotNull(message = "O tipo de resíduo é obrigatório.")
    private TipoResiduo tipoResiduo;

    @JsonProperty("descricao")
    @NotBlank(message = "A descrição do item é obrigatória.")
    @Size(min = 2, max = 255, message = "A descrição deve ter entre 2 e 255 caracteres.")
    private String descricao;

    @JsonProperty("quantidade")
    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade mínima deve ser 1.")
    private Integer quantidade;

    @JsonProperty("volumeEstimadoM3")
    @DecimalMin(value = "0.0", inclusive = false, message = "O volume estimado deve ser maior que 0.")
    private Double volumeEstimadoM3;
}
