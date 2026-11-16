package br.com.ifba.solicitacao.entity;

import br.com.ifba.infraestructure.PersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Foto única do material enviada pelo morador ao criar a solicitação.
 * Fica numa tabela separada para que listagens e o painel não carreguem os bytes da imagem.
 */
@Entity
@Table(name = "fotos_solicitacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FotoSolicitacao extends PersistenceEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_coleta_id", nullable = false, unique = true)
    private SolicitacaoColeta solicitacaoColeta;

    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;

    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] conteudo;
}
