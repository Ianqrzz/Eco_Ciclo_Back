package br.com.ifba.solicitacao.entity;

public enum TipoResiduo {
    MOVEIS("Móveis"),
    ELETRODOMESTICOS("Eletrodomésticos"),
    ELETRONICOS("Eletrônicos"),
    RECICLAVEIS("Recicláveis"),
    ENTULHO_REFORMA("Entulho de reforma limitado a 1 m³"),
    PNEUS("Pneus"),
    ROUPAS("Roupas");

    private final String descricao;

    TipoResiduo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
