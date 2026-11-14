package br.com.ifba.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPutRequestDto {

    @JsonProperty("nome")
    @NotBlank
    @Size(min = 3, max = 255)
    private String nome;

    @JsonProperty("email")
    @Email
    @NotBlank
    private String email;
}