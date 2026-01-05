package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.LoginRequest;
import com.mictlan.economix.sql.dto.UsuarioDto;
import com.mictlan.economix.sql.model.Usuario;
import com.mictlan.economix.sql.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/economix/api/usuarios")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{10,}$");

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
        ResponseEntity<UsuarioDto> validationResponse = validarUsuario(usuarioDto, null);
        if (validationResponse != null) {
            return validationResponse;
        }

        Usuario usuario = usuarioService.save(toEntity(usuarioDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        ResponseEntity<UsuarioDto> validationResponse = validarUsuario(usuarioDto, id);
        if (validationResponse != null) {
            return validationResponse;
        }

        Usuario updated = usuarioService.update(id, toEntity(usuarioDto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginRequest request) {
        if (request == null || !esCorreoValido(request.getCorreo()) || esVacio(request.getContrasena())) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioService.getByCorreo(request.getCorreo());
        if (usuario == null || !usuario.getContrasenaUsuario().equals(request.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(toDto(usuario));
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

    private ResponseEntity<UsuarioDto> validarUsuario(UsuarioDto usuarioDto, Integer idParaActualizar) {
        if (usuarioDto == null || esVacio(usuarioDto.getPerfilUsuario()) || esVacio(usuarioDto.getCorreo())
                || esVacio(usuarioDto.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().build();
        }

        if (!esCorreoValido(usuarioDto.getCorreo()) || !esPasswordValido(usuarioDto.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().build();
        }

        boolean correoEnUso = usuarioService.existsByCorreo(usuarioDto.getCorreo());
        if (correoEnUso) {
            Usuario existente = usuarioService.getByCorreo(usuarioDto.getCorreo());
            if (existente != null && (idParaActualizar == null || !existente.getIdUsuario().equals(idParaActualizar))) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        return null;
    }

    private boolean esCorreoValido(String correo) {
        return correo != null && EMAIL_PATTERN.matcher(correo).matches();
    }

    private boolean esPasswordValido(String contrasena) {
        return contrasena != null && PASSWORD_PATTERN.matcher(contrasena).matches();
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
