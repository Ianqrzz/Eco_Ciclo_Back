package br.com.ifba.solicitacao.service;

import br.com.ifba.solicitacao.entity.FotoSolicitacao;
import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitacaoColetaIService {

    SolicitacaoColeta criarSolicitacao(SolicitacaoColeta solicitacao, Long moradorId);

    /** Cria a solicitação junto com a foto do material (fluxo da tela do cidadão). */
    SolicitacaoColeta criarSolicitacao(SolicitacaoColeta solicitacao, Long moradorId, String fotoContentType, byte[] foto);

    FotoSolicitacao buscarFoto(Long solicitacaoId);

    SolicitacaoColeta findById(Long id);

    SolicitacaoColeta findByProtocolo(String protocolo);

    Page<SolicitacaoColeta> findByMorador(Long moradorId, Pageable pageable);

    SolicitacaoColeta cancelarPeloMorador(Long id, Long moradorId, String motivo);
}
