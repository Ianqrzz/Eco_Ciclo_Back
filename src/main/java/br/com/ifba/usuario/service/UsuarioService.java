package br.com.ifba.usuario.service;


import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UsuarioIService{

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioIRepository usuarioIRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return null;
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Usuario findById(Long id) {
        return null;
    }

    @Override
    public Boolean autenticar(String senha) {
        return null;
    }

    @Override
    public void atualizarSenha(Long id, String senha) {

    }
}
