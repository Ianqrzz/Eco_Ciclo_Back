package br.com.ifba.morador.entity;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Morador extends Usuario {

    private String telefone;
    @OneToOne
    private Endereco endereco;

}
