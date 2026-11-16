package br.com.ifba.morador.serivce;


import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infraestructure.exception.BusinessException;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.repository.MoradorIRepository;
import br.com.ifba.usuario.entity.Perfil;
import br.com.ifba.usuario.repository.UsuarioIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoradorService implements MoradorIService{


    private final MoradorIRepository moradorIRepository;

    private final PasswordEncoder passwordEncoder;

    private final UsuarioIRepository usuarioIRepository;

    @Override
    @Transactional
    public Morador save(Morador morador) {
        // Confere na tabela de usuários inteira, não só entre moradores
        if (usuarioIRepository.existsByEmail(morador.getEmail())) {
            throw new BusinessException("Este e-mail já está cadastrado");
        }

        //criptografa a senha usando Hash unilateral
        // quando for verificar se a senha é a mesma ele vai usar o mesmo metodo de criptografia e vê se da match
        String senhaCriptografada = passwordEncoder.encode(morador.getSenhaHash());
        morador.setSenhaHash(senhaCriptografada);
        morador.setPerfil(Perfil.MORADOR);
        return moradorIRepository.save(morador);
    }

    @Override
    public Page<Morador> findAll(Pageable pageable) {
        return moradorIRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Morador update(Long id, Morador morador) {

        Morador moradorExistente = moradorIRepository.findById(id).orElseThrow(() ->new BusinessException("Usuario não encontrado"));
        moradorExistente.setNome(morador.getNome());
        moradorExistente.setEmail(morador.getEmail());
        moradorExistente.setTelefone(morador.getTelefone());
        moradorExistente.setEndereco(morador.getEndereco());

        Morador moradorAtualizado = moradorIRepository.save(moradorExistente);
        return moradorAtualizado;
    }

    @Override
    public void delete(Long id) {
        moradorIRepository.deleteById(id);
    }
}
