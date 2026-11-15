package br.com.ifba.solicitacao.dto.request;

import br.com.ifba.endereco.dto.EnderecoPostRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoColetaPostRequestDto {

    @JsonProperty("moradorId")
    @NotNull(message = "O ID do morador é obrigatório.")
    private Long moradorId;

    @JsonProperty("enderecoColeta")
    @Valid
    private EnderecoPostRequestDto enderecoColeta;

    @JsonProperty("itens")
    @NotNull(message = "A lista de itens não pode ser nula.")
    @NotEmpty(message = "A solicitação deve conter ao menos um item para coleta.")
    @Valid
    private List<ItemColetaPostRequestDto> itens;

    @JsonProperty("observacoesMorador")
    @Size(max = 1000, message = "As observações do morador devem ter no máximo 1000 caracteres.")
    private String observacoesMorador;
}
