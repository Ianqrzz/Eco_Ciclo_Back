package br.com.ifba.usuario.service;


import br.com.ifba.infraestructure.exception.BusinessException;
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
        logger.info("iniciando o metodo save");
        return usuarioIRepository.save(usuario);
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {

        return usuarioIRepository.findAll(pageable);
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {

        Usuario usuarioExistente = usuarioIRepository.findById(id).orElseThrow(() ->new BusinessException("Usuario não encontrado"));

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setEmail(usuario.getEmail());

        usuarioIRepository.save(usuarioExistente);
        logger.info("usuario atualizado com sucesso");

        return null;
    }

    @Override
    public void delete(Long id) {

        usuarioIRepository.deleteById(id);
        logger.info("usuario deletado com sucesso");
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioIRepository.findById(id).orElseThrow(() -> new BusinessException("Usuario não encontrado"));
    }

    @Override
    public Boolean autenticar(String senha) {


        return null;
    }

    @Override
    public void atualizarSenha(Long id, String senha) {

    }
}
