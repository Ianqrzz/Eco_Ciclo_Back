package br.com.ifba.solicitacao.repository;

import br.com.ifba.solicitacao.entity.ItemColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemColetaIRepository extends JpaRepository<ItemColeta, Long> {
}
