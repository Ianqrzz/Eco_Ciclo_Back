package br.com.ifba.infraestructure;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include @ToString.Include
    @Getter
    private UUID id;
}