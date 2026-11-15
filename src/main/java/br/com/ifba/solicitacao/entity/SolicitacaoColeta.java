package br.com.ifba.solicitacao.entity;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.PersistenceEntity;
import br.com.ifba.morador.entity.Morador;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitacoes_coleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SolicitacaoColeta extends PersistenceEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String protocolo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_coleta_id", nullable = false)
    private Endereco enderecoColeta;

    @OneToMany(mappedBy = "solicitacaoColeta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<ItemColeta> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_agendada")
    private LocalDateTime dataAgendada;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "observacoes_morador", length = 1000)
    private String observacoesMorador;

    @Column(name = "observacoes_equipe", length = 1000)
    private String observacoesEquipe;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @Column(name = "motivo_rejeicao", length = 500)
    private String motivoRejeicao;

    @Column(name = "responsavel_equipe", length = 150)
    private String responsavelEquipe;

    public void adicionarItem(ItemColeta item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
        item.setSolicitacaoColeta(this);
    }

    public void removerItem(ItemColeta item) {
        if (this.itens != null) {
            this.itens.remove(item);
            item.setSolicitacaoColeta(null);
        }
    }
}
