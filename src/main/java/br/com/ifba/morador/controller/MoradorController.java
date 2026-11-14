package br.com.ifba.morador.controller;


import br.com.ifba.infraestructure.mapper.ObjectMapperUtil;
import br.com.ifba.morador.dto.MoradorGetResponseDto;
import br.com.ifba.morador.dto.MoradorRegistroDto;
import br.com.ifba.morador.entity.Morador;
import br.com.ifba.morador.serivce.MoradorIService;
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
@RequestMapping("/morador")
@RequiredArgsConstructor
public class MoradorController {

    private final MoradorIService moradorIService;
    private final UsuarioIService usuarioIService;

    private final ObjectMapperUtil objectMapperUtil;

    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoradorGetResponseDto> save(@RequestBody @Valid MoradorRegistroDto moradorPost){

        Morador morador=  ObjectMapperUtil.map(moradorPost, Morador.class);

        Morador moradorSalvo = moradorIService.save(morador);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ObjectMapperUtil.map(moradorSalvo, MoradorGetResponseDto.class));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MoradorGetResponseDto>> findAll(Pageable pageable) {
        // Busca as entidades puras no Service
        Page<Morador> moradoresPage = moradorIService.findAll(pageable);

        // Converte a página de Entidade para pagina de DTO
        Page<MoradorGetResponseDto> responsePage = moradoresPage.map(morador ->
                ObjectMapperUtil.map(morador, MoradorGetResponseDto.class)
        );

        return ResponseEntity.ok(responsePage);
    }


    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoradorGetResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid MoradorRegistroDto moradorPut) {

        //Converte DTO para Entidade
        Morador morador = ObjectMapperUtil.map(moradorPut, Morador.class);

        //O Service recebe a Entidade para fazer o update
        Morador moradorAtualizado = moradorIService.update(id, morador);

        //Converte a Entidade salva de volta para DTO
        return ResponseEntity.ok(ObjectMapperUtil.map(moradorAtualizado, MoradorGetResponseDto.class));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // O Delete não precisa de DTOs, apenas do ID
        moradorIService.delete(id);

        // Retorna 204 No Content, que é o padrão correto para deleções bem-sucedidas
        return ResponseEntity.noContent().build();
    }
}
