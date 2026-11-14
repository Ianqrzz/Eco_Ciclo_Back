package br.com.ifba.usuario.controller;

import br.com.ifba.infraestructure.mapper.ObjectMapperUtil;
import br.com.ifba.usuario.dto.UsuarioAtualizarSenhaDto;
import br.com.ifba.usuario.dto.UsuarioGetResponseDto;
import br.com.ifba.usuario.dto.UsuarioLoginRequestDto;
import br.com.ifba.usuario.dto.UsuarioPostRequestDto;
import br.com.ifba.usuario.dto.UsuarioPutRequestDto;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController implements UsuarioIController {

    private final UsuarioIService usuarioIService;

    @Override
    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioGetResponseDto> save(@RequestBody @Valid UsuarioPostRequestDto usuarioPost) {
        Usuario usuario = ObjectMapperUtil.map(usuarioPost, Usuario.class);

        usuario.setSenhaHash(usuarioPost.getSenha());

        Usuario usuarioSalvo = usuarioIService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ObjectMapperUtil.map(usuarioSalvo, UsuarioGetResponseDto.class));
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UsuarioGetResponseDto>> findAll(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioIService.findAll(pageable);

        Page<UsuarioGetResponseDto> responsePage = usuariosPage.map(usuario ->
                ObjectMapperUtil.map(usuario, UsuarioGetResponseDto.class)
        );

        return ResponseEntity.ok(responsePage);
    }

    @Override
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioGetResponseDto> findById(@PathVariable Long id) {
        Usuario usuario = usuarioIService.findById(id);
        return ResponseEntity.ok(ObjectMapperUtil.map(usuario, UsuarioGetResponseDto.class));
    }

    @Override
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioGetResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioPutRequestDto usuarioPut) {

        Usuario usuario = ObjectMapperUtil.map(usuarioPut, Usuario.class);
        Usuario usuarioAtualizado = usuarioIService.update(id, usuario);

        return ResponseEntity.ok(ObjectMapperUtil.map(usuarioAtualizado, UsuarioGetResponseDto.class));
    }

    @Override
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioIService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody @Valid UsuarioLoginRequestDto loginRequest) {
        usuarioIService.autenticar(loginRequest.getEmail(), loginRequest.getSenha());
        return ResponseEntity.ok("Login realizado com sucesso.");
    }

    @Override
    @PatchMapping(path = "/{id}/senha", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> atualizarSenha(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioAtualizarSenhaDto senhaDto) {

        usuarioIService.atualizarSenha(id, senhaDto.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}