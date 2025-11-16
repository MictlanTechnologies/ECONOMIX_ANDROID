package com.example.economix_android.sql.controler;

import com.example.economix_android.sql.dto.EstadoDto;
import com.example.economix_android.sql.model.Estado;
import com.example.economix_android.sql.service.EstadoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/Gerdoc/api")
@RestController
@AllArgsConstructor
public class EstadoController {
    private final EstadoService estadoService;

    @RequestMapping("/estado")
    public ResponseEntity<List<EstadoDto>> lista() {
        List<Estado> estados = estadoService.getAll();
        if (estados == null || estados.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok(
                        estados
                                .stream()
                                .map(u -> EstadoDto.builder()
                                        .estado(u.getEstado())
                                        .build())
                                .collect(Collectors.toList()));
    }

    @RequestMapping("/estado/{id}")
    public ResponseEntity<EstadoDto> getById(@PathVariable Integer id) {
        Estado u = estadoService.getById(id);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EstadoDto.builder()
                .estado(u.getEstado())
                .build());
    }

    @PostMapping("/estado")
    public ResponseEntity<EstadoDto> save(@RequestBody EstadoDto estadoDto) {
        Estado u = Estado
                .builder()
                .estado(estadoDto.getEstado())
                .build();
        estadoService.save(u);
        return ResponseEntity.ok(EstadoDto.builder()
                .estado(u.getEstado())
                .build());
    }

    @DeleteMapping("/estado/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<Estado> update(@PathVariable Integer id, @RequestBody EstadoDto estadoDto) {
        Estado aux = estadoService.update(id, Estado
                .builder()
                .estado(estadoDto.getEstado())
                .build());
        if (aux == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Estado.builder()
                .estado(aux.getEstado())
                .build());
    }
}