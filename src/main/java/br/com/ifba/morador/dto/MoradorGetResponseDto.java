package br.com.ifba.morador.dto;


import br.com.ifba.endereco.dto.EnderecoGetResponseDto;
import br.com.ifba.endereco.entity.Endereco;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoradorGetResponseDto {


    @JsonProperty("id")
    private Long id ;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("email")
    private String email;

    @JsonProperty("telefone")
    private String telefone;

    @JsonProperty("endereco")
    private EnderecoGetResponseDto endereco;
}
