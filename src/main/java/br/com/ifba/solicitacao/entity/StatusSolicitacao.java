package br.com.ifba.solicitacao.entity;

public enum StatusSolicitacao {
    PENDENTE("Pendente"),
    EM_ANALISE("Em Análise"),
    ACEITA("Aceita"),
    AGENDADA("Agendada"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isTerminal() {
        return this == CONCLUIDA || this == CANCELADA || this == REJEITADA;
    }
}
