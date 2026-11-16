package br.com.ifba.infraestructure.seed;

import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.repository.MoradorIRepository;
import br.com.ifba.usuario.entity.Perfil;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestUserSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestUserSeeder.class);

    private final UsuarioIRepository usuarioRepository;
    private final MoradorIRepository moradorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        criarUsuarioPrefeituraSeNecessario();
        criarMoradorSeNecessario();
    }

    private void criarUsuarioPrefeituraSeNecessario() {
        String email = "prefeitura@irece.ba.gov.br";
        usuarioRepository.findByEmail(email).ifPresentOrElse(
                u -> {
                    if (u.getPerfil() != Perfil.EQUIPE_MUNICIPAL) {
                        u.setPerfil(Perfil.EQUIPE_MUNICIPAL);
                        usuarioRepository.save(u);
                        logger.info("Perfil do usuário da prefeitura atualizado para EQUIPE_MUNICIPAL: {}", email);
                    } else {
                        logger.info("Conta de teste da prefeitura pronta: {}", email);
                    }
                },
                () -> {
                    Usuario usuario = new Usuario();
                    usuario.setNome("Equipe Municipal de Irecê");
                    usuario.setEmail(email);
                    usuario.setSenhaHash(passwordEncoder.encode("Ecociclo@2026"));
                    usuario.setPerfil(Perfil.EQUIPE_MUNICIPAL);
                    usuarioRepository.save(usuario);
                    logger.info("Conta de teste da prefeitura criada com sucesso: {}", email);
                }
        );
    }

    private void criarMoradorSeNecessario() {
        String email = "ana@exemplo.com";
        usuarioRepository.findByEmail(email).ifPresentOrElse(
                u -> logger.info("Conta de teste do cidadão já existe: {}", email),
                () -> {
                    Morador morador = new Morador();
                    morador.setNome("Ana Silva (Cidadã)");
                    morador.setEmail(email);
                    morador.setSenhaHash(passwordEncoder.encode("Ecociclo@2026"));
                    morador.setPerfil(Perfil.MORADOR);
                    morador.setTelefone("74988776655");
                    moradorRepository.save(morador);
                    logger.info("Conta de teste do cidadão criada com sucesso: {}", email);
                }
        );
    }
}
