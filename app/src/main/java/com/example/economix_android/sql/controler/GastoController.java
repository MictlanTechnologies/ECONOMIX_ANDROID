package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.GastoDto;
import com.example.economix_android.sql.model.Gasto;
import com.example.economix_android.sql.service.GastoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/gastos")
@AllArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    @GetMapping
    public ResponseEntity<List<GastoDto>> getAll() {
        List<Gasto> gastos = gastoService.getAll();
        if (gastos == null || gastos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(gastos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoDto> getById(@PathVariable Integer id) {
        Gasto gasto = gastoService.getById(id);
        if (gasto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(gasto));
    }

    @PostMapping
    public ResponseEntity<GastoDto> save(@RequestBody GastoDto dto) {
        Gasto gasto = gastoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(gasto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GastoDto> update(@PathVariable Integer id, @RequestBody GastoDto dto) {
        Gasto updated = gastoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        gastoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private GastoDto toDto(Gasto gasto) {
        return GastoDto.builder()
                .idGastos(gasto.getIdGastos())
                .idUsuario(gasto.getIdUsuario())
                .idCategoria(gasto.getIdCategoria())
                .idPresupuesto(gasto.getIdPresupuesto())
                .descripcionGasto(gasto.getDescripcionGasto())
                .articuloGasto(gasto.getArticuloGasto())
                .montoGasto(gasto.getMontoGasto())
                .fechaGastos(gasto.getFechaGastos())
                .periodoGastos(gasto.getPeriodoGastos())
                .build();
    }

    private Gasto toEntity(GastoDto dto) {
        return Gasto.builder()
                .idGastos(dto.getIdGastos())
                .idUsuario(dto.getIdUsuario())
                .idCategoria(dto.getIdCategoria())
                .idPresupuesto(dto.getIdPresupuesto())
                .descripcionGasto(dto.getDescripcionGasto())
                .articuloGasto(dto.getArticuloGasto())
                .montoGasto(dto.getMontoGasto())
                .fechaGastos(dto.getFechaGastos())
                .periodoGastos(dto.getPeriodoGastos())
                .build();
    }
}
