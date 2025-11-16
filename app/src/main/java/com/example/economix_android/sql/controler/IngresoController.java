package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.IngresoDto;
import com.example.economix_android.sql.model.Ingreso;
import com.example.economix_android.sql.service.IngresoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/ingresos")
@AllArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

    @GetMapping
    public ResponseEntity<List<IngresoDto>> getAll() {
        List<Ingreso> ingresos = ingresoService.getAll();
        if (ingresos == null || ingresos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ingresos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoDto> getById(@PathVariable Integer id) {
        Ingreso ingreso = ingresoService.getById(id);
        if (ingreso == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(ingreso));
    }

    @PostMapping
    public ResponseEntity<IngresoDto> save(@RequestBody IngresoDto dto) {
        Ingreso ingreso = ingresoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(ingreso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoDto> update(@PathVariable Integer id, @RequestBody IngresoDto dto) {
        Ingreso updated = ingresoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ingresoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private IngresoDto toDto(Ingreso ingreso) {
        return IngresoDto.builder()
                .idIngresos(ingreso.getIdIngresos())
                .idUsuario(ingreso.getIdUsuario())
                .idFuente(ingreso.getIdFuente())
                .montoIngreso(ingreso.getMontoIngreso())
                .periodicidadIngreso(ingreso.getPeriodicidadIngreso())
                .fechaIngresos(ingreso.getFechaIngresos())
                .descripcionIngreso(ingreso.getDescripcionIngreso())
                .build();
    }

    private Ingreso toEntity(IngresoDto dto) {
        return Ingreso.builder()
                .idIngresos(dto.getIdIngresos())
                .idUsuario(dto.getIdUsuario())
                .idFuente(dto.getIdFuente())
                .montoIngreso(dto.getMontoIngreso())
                .periodicidadIngreso(dto.getPeriodicidadIngreso())
                .fechaIngresos(dto.getFechaIngresos())
                .descripcionIngreso(dto.getDescripcionIngreso())
                .build();
    }
}
