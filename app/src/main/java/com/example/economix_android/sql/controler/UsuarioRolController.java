package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.UsuarioRolDto;
import com.example.economix_android.sql.model.UsuarioRol;
import com.example.economix_android.sql.service.UsuarioRolService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/usuario-roles")
@AllArgsConstructor
public class UsuarioRolController {

    private final UsuarioRolService usuarioRolService;

    @GetMapping
    public ResponseEntity<List<UsuarioRolDto>> getAll() {
        List<UsuarioRol> usuarioRoles = usuarioRolService.getAll();
        if (usuarioRoles == null || usuarioRoles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarioRoles.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRolDto> getById(@PathVariable Integer id) {
        UsuarioRol usuarioRol = usuarioRolService.getById(id);
        if (usuarioRol == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(usuarioRol));
    }

    @PostMapping
    public ResponseEntity<UsuarioRolDto> save(@RequestBody UsuarioRolDto dto) {
        UsuarioRol usuarioRol = usuarioRolService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(usuarioRol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRolDto> update(@PathVariable Integer id, @RequestBody UsuarioRolDto dto) {
        UsuarioRol updated = usuarioRolService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioRolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioRolDto toDto(UsuarioRol usuarioRol) {
        return UsuarioRolDto.builder()
                .idUsuarioRol(usuarioRol.getIdUsuarioRol())
                .idUsuario(usuarioRol.getIdUsuario())
                .idRol(usuarioRol.getIdRol())
                .build();
    }

    private UsuarioRol toEntity(UsuarioRolDto dto) {
        return UsuarioRol.builder()
                .idUsuarioRol(dto.getIdUsuarioRol())
                .idUsuario(dto.getIdUsuario())
                .idRol(dto.getIdRol())
                .build();
    }
}
