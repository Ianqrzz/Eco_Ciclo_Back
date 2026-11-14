package br.com.ifba.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoPostRequestDto {

    @NotBlank
    private String cep;

    @NotBlank
    private String logradouro;

    private String numero;


    @NotBlank
    private String bairro;
}
