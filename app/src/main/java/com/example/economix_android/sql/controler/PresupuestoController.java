package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.PresupuestoDto;
import com.example.economix_android.sql.model.Presupuesto;
import com.example.economix_android.sql.service.PresupuestoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/presupuestos")
@AllArgsConstructor
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<List<PresupuestoDto>> getAll() {
        List<Presupuesto> presupuestos = presupuestoService.getAll();
        if (presupuestos == null || presupuestos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(presupuestos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresupuestoDto> getById(@PathVariable Integer id) {
        Presupuesto presupuesto = presupuestoService.getById(id);
        if (presupuesto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(presupuesto));
    }

    @PostMapping
    public ResponseEntity<PresupuestoDto> save(@RequestBody PresupuestoDto dto) {
        Presupuesto presupuesto = presupuestoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(presupuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresupuestoDto> update(@PathVariable Integer id, @RequestBody PresupuestoDto dto) {
        Presupuesto updated = presupuestoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        presupuestoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PresupuestoDto toDto(Presupuesto presupuesto) {
        return PresupuestoDto.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .idUsuario(presupuesto.getIdUsuario())
                .idCategoria(presupuesto.getIdCategoria())
                .categoria(presupuesto.getCategoria())
                .montoMaximo(presupuesto.getMontoMaximo())
                .montoGastado(presupuesto.getMontoGastado())
                .mes(presupuesto.getMes())
                .anio(presupuesto.getAnio())
                .build();
    }

    private Presupuesto toEntity(PresupuestoDto dto) {
        return Presupuesto.builder()
                .idPresupuesto(dto.getIdPresupuesto())
                .idUsuario(dto.getIdUsuario())
                .idCategoria(dto.getIdCategoria())
                .categoria(dto.getCategoria())
                .montoMaximo(dto.getMontoMaximo())
                .montoGastado(dto.getMontoGastado())
                .mes(dto.getMes())
                .anio(dto.getAnio())
                .build();
    }
}
