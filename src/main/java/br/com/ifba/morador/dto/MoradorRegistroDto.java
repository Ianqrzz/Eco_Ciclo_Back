package br.com.ifba.morador.dto;

import br.com.ifba.infraestructure.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MoradorRegistroDto {

    @JsonProperty("nome")
    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String nome;

    @JsonProperty("email")
    @Email
    @NotNull
    @NotBlank
    private String email;

    @JsonProperty("senha")
    @NotNull
    @NotBlank
    @Size(min = 6, max = 255)
    @ValidPassword
    private String senha;

    @JsonProperty("telefone")
    @NotNull
    @NotBlank
    @Size(min = 10, max = 11)
    private String telefone;

}
