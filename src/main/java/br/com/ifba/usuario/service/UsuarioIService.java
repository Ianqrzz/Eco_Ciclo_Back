package br.com.ifba.usuario.service;

import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioIService {

    Usuario save(Usuario usuario);
    Page<Usuario> findAll(Pageable pageable);
    Usuario update(Long id,Usuario usuario);
    void delete(Long id);
    Usuario findById(Long id);
    Boolean autenticar(String email, String senha);
    void atualizarSenha(Long id, String senha);
}
