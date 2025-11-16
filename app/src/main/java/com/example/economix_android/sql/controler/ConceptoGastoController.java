package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.ConceptoGastoDto;
import com.example.economix_android.sql.model.ConceptoGasto;
import com.example.economix_android.sql.service.ConceptoGastoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/conceptos-gasto")
@AllArgsConstructor
public class ConceptoGastoController {

    private final ConceptoGastoService conceptoGastoService;

    @GetMapping
    public ResponseEntity<List<ConceptoGastoDto>> getAll() {
        List<ConceptoGasto> conceptos = conceptoGastoService.getAll();
        if (conceptos == null || conceptos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(conceptos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptoGastoDto> getById(@PathVariable Integer id) {
        ConceptoGasto concepto = conceptoGastoService.getById(id);
        if (concepto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(concepto));
    }

    @PostMapping
    public ResponseEntity<ConceptoGastoDto> save(@RequestBody ConceptoGastoDto dto) {
        ConceptoGasto concepto = conceptoGastoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(concepto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptoGastoDto> update(@PathVariable Integer id, @RequestBody ConceptoGastoDto dto) {
        ConceptoGasto updated = conceptoGastoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        conceptoGastoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ConceptoGastoDto toDto(ConceptoGasto concepto) {
        return ConceptoGastoDto.builder()
                .idConcepto(concepto.getIdConcepto())
                .nombreConcepto(concepto.getNombreConcepto())
                .descripcionConcepto(concepto.getDescripcionConcepto())
                .precioConcepto(concepto.getPrecioConcepto())
                .idGastos(concepto.getIdGastos())
                .build();
    }

    private ConceptoGasto toEntity(ConceptoGastoDto dto) {
        return ConceptoGasto.builder()
                .idConcepto(dto.getIdConcepto())
                .nombreConcepto(dto.getNombreConcepto())
                .descripcionConcepto(dto.getDescripcionConcepto())
                .precioConcepto(dto.getPrecioConcepto())
                .idGastos(dto.getIdGastos())
                .build();
    }
}
