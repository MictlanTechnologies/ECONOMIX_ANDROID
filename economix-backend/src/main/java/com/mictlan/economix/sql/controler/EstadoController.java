package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.EstadoDto;
import com.mictlan.economix.sql.model.Estado;
import com.mictlan.economix.sql.service.EstadoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/estados")
@AllArgsConstructor
public class EstadoController {
    private final EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<EstadoDto>> lista() {
        List<Estado> estados = estadoService.getAll();
        if (estados == null || estados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(estados.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoDto> getById(@PathVariable Integer id) {
        Estado u = estadoService.getById(id);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(u));
    }

    @PostMapping
    public ResponseEntity<EstadoDto> save(@RequestBody EstadoDto estadoDto) {
        Estado saved = estadoService.save(toEntity(estadoDto));
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoDto> update(@PathVariable Integer id, @RequestBody EstadoDto estadoDto) {
        Estado aux = estadoService.update(id, toEntity(estadoDto));
        if (aux == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(aux));
    }

    private EstadoDto toDto(Estado estado) {
        return EstadoDto.builder()
                .id(estado.getId())
                .estado(estado.getEstado())
                .build();
    }

    private Estado toEntity(EstadoDto dto) {
        return Estado.builder()
                .id(dto.getId())
                .estado(dto.getEstado())
                .build();
    }
}
