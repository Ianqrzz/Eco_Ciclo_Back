package br.com.ifba.usuario.service;


import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UsuarioIService{

    //provavelmente vou ter que mudar o jeito que essa classe funciona por causa da herança de morador e etc.

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioIRepository usuarioIRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario save(Usuario usuario) {

        //criptografa a senha usando Hash unilateral
        // quando for verificar se a senha é a mesma ele vai usar o mesmo metodo de criptografia e vê se da match
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenhaHash());
        usuario.setSenhaHash(senhaCriptografada);
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

        Usuario usuarioAtualizado = usuarioIRepository.save(usuarioExistente);
        logger.info("usuario atualizado com sucesso");

        return usuarioAtualizado;
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

        //luiz quando for fazer a parte do login usa esse metodo aqui

        return null;
    }

    @Override
    public void atualizarSenha(Long id, String senha) {

    }
}
