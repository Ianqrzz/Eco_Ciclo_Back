package br.com.ifba.usuario.controller;

import br.com.ifba.usuario.dto.UsuarioAtualizarSenhaDto;
import br.com.ifba.usuario.dto.UsuarioGetResponseDto;
import br.com.ifba.usuario.dto.UsuarioLoginRequestDto;
import br.com.ifba.usuario.dto.UsuarioPostRequestDto;
import br.com.ifba.usuario.dto.UsuarioPutRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface UsuarioIController {

    ResponseEntity<UsuarioGetResponseDto> save(UsuarioPostRequestDto usuarioPost);
    ResponseEntity<Page<UsuarioGetResponseDto>> findAll(Pageable pageable);
    ResponseEntity<UsuarioGetResponseDto> findById(Long id);
    ResponseEntity<UsuarioGetResponseDto> update(Long id, UsuarioPutRequestDto usuarioPut);
    ResponseEntity<Void> delete(Long id);
    ResponseEntity<String> login(UsuarioLoginRequestDto loginRequest);
    ResponseEntity<Void> atualizarSenha(Long id, UsuarioAtualizarSenhaDto senhaDto);
}