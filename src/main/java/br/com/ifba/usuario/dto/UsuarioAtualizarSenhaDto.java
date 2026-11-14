package br.com.ifba.usuario.dto;

import br.com.ifba.infraestructure.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAtualizarSenhaDto {

    @JsonProperty("novaSenha")
    @NotBlank
    @Size(min = 6, max = 255)
    @ValidPassword
    private String novaSenha;
}