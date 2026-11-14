package br.com.ifba.morador.repository;

import br.com.ifba.morador.entity.Morador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoradorIRepository extends JpaRepository<Morador, Long> {


}
