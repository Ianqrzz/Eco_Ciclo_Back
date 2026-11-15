package br.com.ifba.solicitacao.repository;

import br.com.ifba.solicitacao.entity.SolicitacaoColeta;
import br.com.ifba.solicitacao.entity.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoColetaIRepository extends JpaRepository<SolicitacaoColeta, Long>, JpaSpecificationExecutor<SolicitacaoColeta> {

    Optional<SolicitacaoColeta> findByProtocolo(String protocolo);

    boolean existsByProtocolo(String protocolo);

    Page<SolicitacaoColeta> findByMoradorId(Long moradorId, Pageable pageable);

    long countByStatus(StatusSolicitacao status);

    @Query("SELECT i.tipoResiduo, COUNT(DISTINCT s) FROM SolicitacaoColeta s JOIN s.itens i GROUP BY i.tipoResiduo")
    List<Object[]> countSolicitacoesByTipoResiduo();

    @Query("SELECT e.bairro, COUNT(s) FROM SolicitacaoColeta s JOIN s.enderecoColeta e WHERE e.bairro IS NOT NULL AND TRIM(e.bairro) <> '' GROUP BY e.bairro")
    List<Object[]> countSolicitacoesByBairro();
}
