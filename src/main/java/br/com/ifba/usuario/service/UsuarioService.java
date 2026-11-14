package br.com.ifba.usuario.service;


import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UsuarioService implements UsuarioIService{

    //provavelmente vou ter que mudar o jeito que essa classe funciona por causa da herança de morador e etc.

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private static final int MAX_TENTATIVAS = 5;
    private static final int JANELA_TENTATIVAS_MINUTOS = 10;
    private static final int TEMPO_BLOQUEIO_MINUTOS = 15;

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
    @Transactional(noRollbackFor = BusinessException.class)
    public Boolean autenticar(String email, String senha) {
        Usuario usuario = usuarioIRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos."));

        LocalDateTime agora = LocalDateTime.now();


        if (usuario.getBloqueadoAte() != null) {
            if (agora.isBefore(usuario.getBloqueadoAte())) {
                long minutosRestantes = Duration.between(agora, usuario.getBloqueadoAte()).toMinutes() + 1;
                throw new BusinessException("Conta bloqueada temporariamente. Tente novamente em " + minutosRestantes + " minuto(s).");
            } else {

                usuario.setBloqueadoAte(null);
            }
        }


        if (passwordEncoder.matches(senha, usuario.getSenhaHash())) {

            usuario.setTentativasFalhas(0);
            usuario.setPrimeiraTentativaFalha(null);
            usuario.setBloqueadoAte(null);
            usuarioIRepository.save(usuario);

            logger.info("Login realizado com sucesso para: {}", email);
            return true;
        }


        registrarTentativaFalha(usuario, agora);
        return false;
    }

    private void registrarTentativaFalha(Usuario usuario, LocalDateTime agora) {
        boolean janelaExpirada = usuario.getPrimeiraTentativaFalha() == null ||
                agora.isAfter(usuario.getPrimeiraTentativaFalha().plusMinutes(JANELA_TENTATIVAS_MINUTOS));

        if (janelaExpirada) {
            usuario.setPrimeiraTentativaFalha(agora);
            usuario.setTentativasFalhas(1);
        } else {

            usuario.setTentativasFalhas(usuario.getTentativasFalhas() + 1);
        }


        if (usuario.getTentativasFalhas() >= MAX_TENTATIVAS) {
            usuario.setBloqueadoAte(agora.plusMinutes(TEMPO_BLOQUEIO_MINUTOS));
            usuario.setTentativasFalhas(0);
            usuario.setPrimeiraTentativaFalha(null);
            usuarioIRepository.save(usuario);

            logger.warn("Usuário {} bloqueado por {} minutos após excesso de tentativas.", usuario.getEmail(), TEMPO_BLOQUEIO_MINUTOS);
            throw new BusinessException("Limite de tentativas excedido. Usuário bloqueado por 15 minutos.");
        }

        usuarioIRepository.save(usuario);
        int restantes = MAX_TENTATIVAS - usuario.getTentativasFalhas();
        throw new BusinessException("E-mail ou senha inválidos. Tentativas restantes: " + restantes);
    }

    @Override
    public void atualizarSenha(Long id, String novaSenha) {
        Usuario usuario = findById(id);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioIRepository.save(usuario);
        logger.info("Senha atualizada com sucesso para o usuário: {}", id);
    }
}
