package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.MovimientoAhorroDto;
import com.mictlan.economix.sql.model.MovimientoAhorro;
import com.mictlan.economix.sql.service.MovimientoAhorroService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/movimientos-ahorro")
@AllArgsConstructor
public class MovimientoAhorroController {

    private final MovimientoAhorroService movimientoAhorroService;

    @GetMapping
    public ResponseEntity<List<MovimientoAhorroDto>> getAll() {
        List<MovimientoAhorro> movimientos = movimientoAhorroService.getAll();
        if (movimientos == null || movimientos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movimientos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoAhorroDto> getById(@PathVariable Integer id) {
        MovimientoAhorro movimiento = movimientoAhorroService.getById(id);
        if (movimiento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(movimiento));
    }

    @PostMapping
    public ResponseEntity<MovimientoAhorroDto> save(@RequestBody MovimientoAhorroDto dto) {
        if (!isValid(dto)) {
            return ResponseEntity.badRequest().build();
        }
        MovimientoAhorro movimiento = movimientoAhorroService.save(toEntity(dto));
        if (movimiento == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toDto(movimiento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoAhorroDto> update(@PathVariable Integer id, @RequestBody MovimientoAhorroDto dto) {
        if (!isValid(dto)) {
            return ResponseEntity.badRequest().build();
        }
        MovimientoAhorro updated = movimientoAhorroService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = movimientoAhorroService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private MovimientoAhorroDto toDto(MovimientoAhorro movimiento) {
        return MovimientoAhorroDto.builder()
                .idMovimiento(movimiento.getIdMovimiento())
                .idAhorro(movimiento.getIdAhorro())
                .idUsuario(movimiento.getIdUsuario())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .monto(movimiento.getMonto())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .nota(movimiento.getNota())
                .build();
    }

    private MovimientoAhorro toEntity(MovimientoAhorroDto dto) {
        return MovimientoAhorro.builder()
                .idMovimiento(dto.getIdMovimiento())
                .idAhorro(dto.getIdAhorro())
                .idUsuario(dto.getIdUsuario())
                .tipoMovimiento(dto.getTipoMovimiento())
                .monto(dto.getMonto())
                .fechaMovimiento(dto.getFechaMovimiento())
                .nota(dto.getNota())
                .build();
    }

    private boolean isValid(MovimientoAhorroDto dto) {
        if (dto == null || dto.getIdAhorro() == null || dto.getIdUsuario() == null) {
            return false;
        }
        if (dto.getMonto() == null || dto.getMonto().signum() <= 0) {
            return false;
        }
        String tipo = dto.getTipoMovimiento();
        return "APORTE".equalsIgnoreCase(tipo) || "RETIRO".equalsIgnoreCase(tipo);
    }
}
