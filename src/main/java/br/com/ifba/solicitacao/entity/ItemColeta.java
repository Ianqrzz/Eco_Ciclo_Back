package br.com.ifba.solicitacao.entity;

import br.com.ifba.infraestructure.PersistenceEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "itens_coleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemColeta extends PersistenceEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false, length = 30)
    private TipoResiduo tipoResiduo;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "volume_estimado_m3")
    private Double volumeEstimadoM3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_coleta_id")
    @JsonBackReference
    private SolicitacaoColeta solicitacaoColeta;
}
