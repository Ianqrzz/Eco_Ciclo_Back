package br.com.ifba.usuario.dto;


import br.com.ifba.infraestructure.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPostRequestDto {

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

}
