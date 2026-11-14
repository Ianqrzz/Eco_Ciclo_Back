package br.com.ifba.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginRequestDto {

    @JsonProperty("email")
    @Email
    @NotBlank
    private String email;

    @JsonProperty("senha")
    @NotBlank
    private String senha;
}