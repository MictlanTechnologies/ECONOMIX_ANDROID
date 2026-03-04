package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.CategoriaGastoDto;
import com.mictlan.economix.sql.model.CategoriaGasto;
import com.mictlan.economix.sql.service.CategoriaGastoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/categorias-gasto")
@AllArgsConstructor
public class CategoriaGastoController {

    private final CategoriaGastoService categoriaGastoService;

    @GetMapping
    public ResponseEntity<List<CategoriaGastoDto>> getAll() {
        List<CategoriaGasto> categorias = categoriaGastoService.getAll();
        if (categorias == null || categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categorias.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaGastoDto> getById(@PathVariable Integer id) {
        CategoriaGasto categoria = categoriaGastoService.getById(id);
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(categoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaGastoDto> save(@RequestBody CategoriaGastoDto dto) {
        CategoriaGasto categoria = categoriaGastoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaGastoDto> update(@PathVariable Integer id, @RequestBody CategoriaGastoDto dto) {
        CategoriaGasto updated = categoriaGastoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoriaGastoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaGastoDto toDto(CategoriaGasto categoria) {
        return CategoriaGastoDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .idUsuario(categoria.getIdUsuario())
                .nombreCategoria(categoria.getNombreCategoria())
                .descripcion(categoria.getDescripcion())
                .color(categoria.getColor())
                .build();
    }

    private CategoriaGasto toEntity(CategoriaGastoDto dto) {
        return CategoriaGasto.builder()
                .idCategoria(dto.getIdCategoria())
                .idUsuario(dto.getIdUsuario())
                .nombreCategoria(dto.getNombreCategoria())
                .descripcion(dto.getDescripcion())
                .color(dto.getColor())
                .build();
    }
}
