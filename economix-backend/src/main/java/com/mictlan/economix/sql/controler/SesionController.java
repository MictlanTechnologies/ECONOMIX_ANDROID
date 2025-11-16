package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.SesionDto;
import com.mictlan.economix.sql.model.Sesion;
import com.mictlan.economix.sql.service.SesionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/sesiones")
@AllArgsConstructor
public class SesionController {

    private final SesionService sesionService;

    @GetMapping
    public ResponseEntity<List<SesionDto>> getAll() {
        List<Sesion> sesiones = sesionService.getAll();
        if (sesiones == null || sesiones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sesiones.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionDto> getById(@PathVariable Long id) {
        Sesion sesion = sesionService.getById(id);
        if (sesion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(sesion));
    }

    @PostMapping
    public ResponseEntity<SesionDto> save(@RequestBody SesionDto dto) {
        Sesion sesion = sesionService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(sesion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SesionDto> update(@PathVariable Long id, @RequestBody SesionDto dto) {
        Sesion updated = sesionService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sesionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SesionDto toDto(Sesion sesion) {
        return SesionDto.builder()
                .idSesion(sesion.getIdSesion())
                .idUsuario(sesion.getIdUsuario())
                .token(sesion.getToken())
                .ipOrigen(sesion.getIpOrigen())
                .dispositivo(sesion.getDispositivo())
                .fechaInicio(sesion.getFechaInicio())
                .fechaUltimoAcceso(sesion.getFechaUltimoAcceso())
                .vigente(sesion.getVigente())
                .build();
    }

    private Sesion toEntity(SesionDto dto) {
        return Sesion.builder()
                .idSesion(dto.getIdSesion())
                .idUsuario(dto.getIdUsuario())
                .token(dto.getToken())
                .ipOrigen(dto.getIpOrigen())
                .dispositivo(dto.getDispositivo())
                .fechaInicio(dto.getFechaInicio())
                .fechaUltimoAcceso(dto.getFechaUltimoAcceso())
                .vigente(dto.getVigente())
                .build();
    }
}
