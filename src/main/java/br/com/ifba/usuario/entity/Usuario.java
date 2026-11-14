package br.com.ifba.usuario.entity;

import br.com.ifba.infraestructure.PersistenceEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario extends PersistenceEntity {
    private String nome;
    private String email;
    private String senhaHash;
    private int tentativasFalhas;
    private LocalDateTime bloqueadoAte;
    private Perfil perfil;

}
