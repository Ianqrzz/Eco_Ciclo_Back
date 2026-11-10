package br.com.ifba.usuario.entity;

import br.com.ifba.infraestructure.PersistenceEntity;

import java.time.LocalDateTime;

public class Usuario extends PersistenceEntity {
    private String nome;
    private String email;
    private String senhaHash;
    private int tentativasFalhas;
    private LocalDateTime bloqueadoAte;

}
