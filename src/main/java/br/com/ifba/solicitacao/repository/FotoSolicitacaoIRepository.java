package br.com.ifba.solicitacao.repository;

import br.com.ifba.solicitacao.entity.FotoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FotoSolicitacaoIRepository extends JpaRepository<FotoSolicitacao, Long> {

    Optional<FotoSolicitacao> findBySolicitacaoColetaId(Long solicitacaoColetaId);
}
