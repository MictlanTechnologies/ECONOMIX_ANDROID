package com.mictlan.economix.sql.controler;

import com.mictlan.economix.sql.dto.ContactoDto;
import com.mictlan.economix.sql.model.Contacto;
import com.mictlan.economix.sql.service.ContactoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/economix/api/contactos")
@AllArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @GetMapping
    public ResponseEntity<List<ContactoDto>> getAll() {
        List<Contacto> contactos = contactoService.getAll();
        if (contactos == null || contactos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contactos.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactoDto> getById(@PathVariable Integer id) {
        Contacto contacto = contactoService.getById(id);
        if (contacto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(contacto));
    }

    @PostMapping
    public ResponseEntity<ContactoDto> save(@RequestBody ContactoDto dto) {
        Contacto contacto = contactoService.save(toEntity(dto));
        return ResponseEntity.ok(toDto(contacto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactoDto> update(@PathVariable Integer id, @RequestBody ContactoDto dto) {
        Contacto updated = contactoService.update(id, toEntity(dto));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contactoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ContactoDto toDto(Contacto contacto) {
        return ContactoDto.builder()
                .idContactos(contacto.getIdContactos())
                .numCelular(contacto.getNumCelular())
                .correoAlterno(contacto.getCorreoAlterno())
                .idPersona(contacto.getIdPersona())
                .build();
    }

    private Contacto toEntity(ContactoDto dto) {
        return Contacto.builder()
                .idContactos(dto.getIdContactos())
                .numCelular(dto.getNumCelular())
                .correoAlterno(dto.getCorreoAlterno())
                .idPersona(dto.getIdPersona())
                .build();
    }
}
