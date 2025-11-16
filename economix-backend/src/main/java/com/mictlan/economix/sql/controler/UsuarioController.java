package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.UsuarioDto;
import com.mictlan.economix.sql.model.Usuario;
import com.mictlan.economix.sql.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> getAll() {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable Integer id) {
        Usuario usuario = usuarioService.getById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        Usuario usuario = usuarioService.save(toEntity(usuarioDto));
        return ResponseEntity.ok(toDto(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        Usuario updated = usuarioService.update(id, toEntity(usuarioDto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDto toDto(Usuario usuario) {
        return UsuarioDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .perfilUsuario(usuario.getPerfilUsuario())
                .correo(usuario.getCorreo())
                .contrasenaUsuario(usuario.getContrasenaUsuario())
                .fechaRegistro(usuario.getFechaRegistro())
                .estado(usuario.getEstado())
                .build();
    }

    private Usuario toEntity(UsuarioDto dto) {
        return Usuario.builder()
                .idUsuario(dto.getIdUsuario())
                .perfilUsuario(dto.getPerfilUsuario())
                .correo(dto.getCorreo())
                .contrasenaUsuario(dto.getContrasenaUsuario())
                .fechaRegistro(dto.getFechaRegistro())
                .estado(dto.getEstado())
                .build();
    }
}
