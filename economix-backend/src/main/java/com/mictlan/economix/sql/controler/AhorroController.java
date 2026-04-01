package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.AhorroDto;
import com.mictlan.economix.sql.model.Ahorro;
import com.mictlan.economix.sql.service.AhorroService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/ahorros")
@AllArgsConstructor
public class AhorroController {

    private final AhorroService ahorroService;

    @GetMapping
    public ResponseEntity<List<AhorroDto>> getAll() {
        List<Ahorro> ahorros = ahorroService.getAll();
        if (ahorros == null || ahorros.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ahorros.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AhorroDto> getById(@PathVariable Integer id) {
        Ahorro ahorro = ahorroService.getById(id);
        if (ahorro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(ahorro));
    }

    @PostMapping
    public ResponseEntity<AhorroDto> save(@RequestBody AhorroDto ahorroDto) {
        Ahorro ahorro = ahorroService.save(toEntity(ahorroDto));
        return ResponseEntity.ok(toDto(ahorro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AhorroDto> update(@PathVariable Integer id, @RequestBody AhorroDto ahorroDto) {
        Ahorro updated = ahorroService.update(id, toEntity(ahorroDto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ahorroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AhorroDto toDto(Ahorro ahorro) {
        return AhorroDto.builder()
                .idAhorro(ahorro.getIdAhorro())
                .idUsuario(ahorro.getIdUsuario())
                .nombreObjetivo(ahorro.getNombreObjetivo())
                .descripcionObjetivo(ahorro.getDescripcionObjetivo())
                .meta(ahorro.getMeta())
                .montoAhorrado(ahorro.getMontoAhorrado())
                .fechaLimite(ahorro.getFechaLimite())
                .build();
    }

    private Ahorro toEntity(AhorroDto dto) {
        return Ahorro.builder()
                .idAhorro(dto.getIdAhorro())
                .idUsuario(dto.getIdUsuario())
                .nombreObjetivo(dto.getNombreObjetivo())
                .descripcionObjetivo(dto.getDescripcionObjetivo())
                .meta(dto.getMeta())
                .montoAhorrado(dto.getMontoAhorrado())
                .fechaLimite(dto.getFechaLimite())
                .build();
    }
}
