package br.com.ifba.morador.serivce;

import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.repository.MoradorIRepository;
import br.com.ifba.usuario.entity.Perfil;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoradorServiceTest {

    @Mock
    private MoradorIRepository moradorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioIRepository usuarioRepository;

    @InjectMocks
    private MoradorService service;

    private static Morador novoMorador() {
        Morador m = new Morador();
        m.setNome("Maria");
        m.setEmail("maria@ecociclo.dev");
        m.setSenhaHash("Senha@123");
        return m;
    }

    @Test
    void save_criptografaSenhaEDefinePerfilMorador() {
        when(usuarioRepository.existsByEmail("maria@ecociclo.dev")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash");
        when(moradorRepository.save(any(Morador.class))).thenAnswer(inv -> inv.getArgument(0));

        Morador salvo = service.save(novoMorador());

        assertThat(salvo.getSenhaHash()).isEqualTo("hash");
        assertThat(salvo.getPerfil()).isEqualTo(Perfil.MORADOR);
    }

    @Test
    void save_comEmailJaCadastrado_lancaExcecao() {
        when(usuarioRepository.existsByEmail("maria@ecociclo.dev")).thenReturn(true);

        assertThatThrownBy(() -> service.save(novoMorador()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Este e-mail já está cadastrado");
        verify(moradorRepository, never()).save(any());
    }
}
