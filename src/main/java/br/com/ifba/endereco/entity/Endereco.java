package br.com.ifba.endereco.entity;


import br.com.ifba.infraestructure.PersistenceEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Endereco extends PersistenceEntity {
    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
}
