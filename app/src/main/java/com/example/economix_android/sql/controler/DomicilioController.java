package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.DomicilioDto;
import com.example.economix_android.sql.model.Domicilio;
import com.example.economix_android.sql.service.DomicilioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/domicilios")
@AllArgsConstructor
public class DomicilioController {

    private final DomicilioService domicilioService;

    @GetMapping
    public ResponseEntity<List<DomicilioDto>> getAll() {
        List<Domicilio> domicilios = domicilioService.getAll();
        if (domicilios == null || domicilios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(domicilios.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomicilioDto> getById(@PathVariable Integer id) {
        Domicilio domicilio = domicilioService.getById(id);
        if (domicilio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(domicilio));
    }

    @PostMapping
    public ResponseEntity<DomicilioDto> save(@RequestBody DomicilioDto dto) {
        Domicilio domicilio = domicilioService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(domicilio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DomicilioDto> update(@PathVariable Integer id, @RequestBody DomicilioDto dto) {
        Domicilio updated = domicilioService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        domicilioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private DomicilioDto toDto(Domicilio domicilio) {
        return DomicilioDto.builder()
                .idDomicilio(domicilio.getIdDomicilio())
                .ciudad(domicilio.getCiudad())
                .calle(domicilio.getCalle())
                .colonia(domicilio.getColonia())
                .numero(domicilio.getNumero())
                .codigoPostal(domicilio.getCodigoPostal())
                .idPersona(domicilio.getIdPersona())
                .build();
    }

    private Domicilio toEntity(DomicilioDto dto) {
        return Domicilio.builder()
                .idDomicilio(dto.getIdDomicilio())
                .ciudad(dto.getCiudad())
                .calle(dto.getCalle())
                .colonia(dto.getColonia())
                .numero(dto.getNumero())
                .codigoPostal(dto.getCodigoPostal())
                .idPersona(dto.getIdPersona())
                .build();
    }
}
