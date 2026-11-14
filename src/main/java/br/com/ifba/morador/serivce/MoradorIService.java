package br.com.ifba.morador.serivce;

import br.com.ifba.morador.entity.Morador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MoradorIService {

    public Morador save(Morador morador);
    public Page<Morador> findAll(Pageable pageable);
    public Morador update(Long id,Morador morador);
    public void delete(Long id);

}
