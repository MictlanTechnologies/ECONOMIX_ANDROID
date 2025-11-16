package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.ConceptoIngresoDto;
import com.example.economix_android.sql.model.ConceptoIngreso;
import com.example.economix_android.sql.service.ConceptoIngresoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/conceptos-ingreso")
@AllArgsConstructor
public class ConceptoIngresoController {

    private final ConceptoIngresoService conceptoIngresoService;

    @GetMapping
    public ResponseEntity<List<ConceptoIngresoDto>> getAll() {
        List<ConceptoIngreso> conceptos = conceptoIngresoService.getAll();
        if (conceptos == null || conceptos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(conceptos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptoIngresoDto> getById(@PathVariable Integer id) {
        ConceptoIngreso concepto = conceptoIngresoService.getById(id);
        if (concepto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(concepto));
    }

    @PostMapping
    public ResponseEntity<ConceptoIngresoDto> save(@RequestBody ConceptoIngresoDto dto) {
        ConceptoIngreso conceptoIngreso = conceptoIngresoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(conceptoIngreso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptoIngresoDto> update(@PathVariable Integer id, @RequestBody ConceptoIngresoDto dto) {
        ConceptoIngreso updated = conceptoIngresoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        conceptoIngresoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ConceptoIngresoDto toDto(ConceptoIngreso conceptoIngreso) {
        return ConceptoIngresoDto.builder()
                .idConcepto(conceptoIngreso.getIdConcepto())
                .nombreConcepto(conceptoIngreso.getNombreConcepto())
                .descripcionConcepto(conceptoIngreso.getDescripcionConcepto())
                .precioConcepto(conceptoIngreso.getPrecioConcepto())
                .idIngresos(conceptoIngreso.getIdIngresos())
                .build();
    }

    private ConceptoIngreso toEntity(ConceptoIngresoDto dto) {
        return ConceptoIngreso.builder()
                .idConcepto(dto.getIdConcepto())
                .nombreConcepto(dto.getNombreConcepto())
                .descripcionConcepto(dto.getDescripcionConcepto())
                .precioConcepto(dto.getPrecioConcepto())
                .idIngresos(dto.getIdIngresos())
                .build();
    }
}
