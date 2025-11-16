package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.RolDto;
import com.mictlan.economix.sql.model.Rol;
import com.mictlan.economix.sql.service.RolService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/roles")
@AllArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDto>> getAll() {
        List<Rol> roles = rolService.getAll();
        if (roles == null || roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDto> getById(@PathVariable Integer id) {
        Rol rol = rolService.getById(id);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(rol));
    }

    @PostMapping
    public ResponseEntity<RolDto> save(@RequestBody RolDto dto) {
        Rol rol = rolService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(rol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDto> update(@PathVariable Integer id, @RequestBody RolDto dto) {
        Rol updated = rolService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RolDto toDto(Rol rol) {
        return RolDto.builder()
                .idRol(rol.getIdRol())
                .nombreRol(rol.getNombreRol())
                .descripcion(rol.getDescripcion())
                .build();
    }

    private Rol toEntity(RolDto dto) {
        return Rol.builder()
                .idRol(dto.getIdRol())
                .nombreRol(dto.getNombreRol())
                .descripcion(dto.getDescripcion())
                .build();
    }
}
