package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.FuenteIngresoDto;
import com.example.economix_android.sql.model.FuenteIngreso;
import com.example.economix_android.sql.service.FuenteIngresoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/fuentes-ingreso")
@AllArgsConstructor
public class FuenteIngresoController {

    private final FuenteIngresoService fuenteIngresoService;

    @GetMapping
    public ResponseEntity<List<FuenteIngresoDto>> getAll() {
        List<FuenteIngreso> fuentes = fuenteIngresoService.getAll();
        if (fuentes == null || fuentes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fuentes.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuenteIngresoDto> getById(@PathVariable Integer id) {
        FuenteIngreso fuenteIngreso = fuenteIngresoService.getById(id);
        if (fuenteIngreso == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(fuenteIngreso));
    }

    @PostMapping
    public ResponseEntity<FuenteIngresoDto> save(@RequestBody FuenteIngresoDto dto) {
        FuenteIngreso fuenteIngreso = fuenteIngresoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(fuenteIngreso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuenteIngresoDto> update(@PathVariable Integer id, @RequestBody FuenteIngresoDto dto) {
        FuenteIngreso updated = fuenteIngresoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fuenteIngresoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private FuenteIngresoDto toDto(FuenteIngreso fuenteIngreso) {
        return FuenteIngresoDto.builder()
                .idFuente(fuenteIngreso.getIdFuente())
                .idUsuario(fuenteIngreso.getIdUsuario())
                .nombreFuente(fuenteIngreso.getNombreFuente())
                .descripcion(fuenteIngreso.getDescripcion())
                .activo(fuenteIngreso.getActivo())
                .build();
    }

    private FuenteIngreso toEntity(FuenteIngresoDto dto) {
        return FuenteIngreso.builder()
                .idFuente(dto.getIdFuente())
                .idUsuario(dto.getIdUsuario())
                .nombreFuente(dto.getNombreFuente())
                .descripcion(dto.getDescripcion())
                .activo(dto.getActivo())
                .build();
    }
}
